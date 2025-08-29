package com.example.demo.services.Implementation;

import com.example.demo.dto.response.AttendanceMatrixResponse;
import com.example.demo.dto.response.SessionResponse;
import com.example.demo.models.*;
import com.example.demo.models.enums.AttendanceSessionStatus;
import com.example.demo.models.enums.StudentAttendanceStatus;
import com.example.demo.repository.AttendanceSessionRepo;
import com.example.demo.repository.StudentAttendanceRepo;
import com.example.demo.repository.StudentPreCheckinRepo;
import com.example.demo.repository.StudentRepo;
import com.example.demo.services.Interface.AttendanceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class AttendanceServiceImpl implements AttendanceService {

  private final AttendanceSessionRepo sessionRepo;
  private final StudentAttendanceRepo attendanceRepo;
  private final StudentPreCheckinRepo preRepo;
  private final StudentRepo studentRepo;

  @PersistenceContext private EntityManager em;

  public AttendanceServiceImpl(AttendanceSessionRepo sessionRepo,
                               StudentAttendanceRepo attendanceRepo,
                               StudentPreCheckinRepo preRepo,
                               StudentRepo studentRepo) {
    this.sessionRepo = sessionRepo;
    this.attendanceRepo = attendanceRepo;
    this.preRepo = preRepo;
    this.studentRepo = studentRepo;
  }

  // --------- helpers ---------
  private <T> T mustFind(Class<T> type, Long id){
    T x = em.find(type, id);
    if (x==null) throw new ResponseStatusException(NOT_FOUND, type.getSimpleName()+" not found: "+id);
    return x;
  }

  /** Reflection-safe read of optional LocalDate accessors. */
  private LocalDate getLocalDateIfPresent(Object obj, String... methodNames) {
    for (String name : methodNames) {
      try {
        Method m = obj.getClass().getMethod(name);
        Object v = m.invoke(obj);
        if (v instanceof LocalDate d) return d;
      } catch (NoSuchMethodException ignore) {
      } catch (Exception e) {
        throw new ResponseStatusException(BAD_REQUEST, "Cannot read schedule field: " + name, e);
      }
    }
    return null;
  }

  private Method findMethodOrNull(Class<?> type, String... names) {
    for (String n : names) {
      try { return type.getMethod(n); }
      catch (NoSuchMethodException ignore) {}
    }
    return null;
  }

  private Object invoke(Object target, Method m) {
    try { return m.invoke(target); }
    catch (Exception e) { throw new ResponseStatusException(BAD_REQUEST, "Validation failed", e); }
  }

  /** Validate that the schedule occurrence exists on the given date (calendar check). */
  private void assertScheduleMatchesDate(GroupSchedule sched, LocalDate date){
    // 1) Day-of-week matches (if entity exposes it)
    Method mDow = findMethodOrNull(sched.getClass(), "getDayOfWeek");
    if (mDow != null) {
      Object v = invoke(sched, mDow);
      if (v != null) {
        DayOfWeek expected = DayOfWeek.valueOf(v.toString().toUpperCase());
        if (!expected.equals(date.getDayOfWeek())) {
          throw new ResponseStatusException(BAD_REQUEST,
              "Schedule does not run on " + date.getDayOfWeek() + " (schedule day: " + expected + ")");
        }
      }
    }

    // 2) Effective window (if present on entity)
    LocalDate from = getLocalDateIfPresent(sched, "getStartDate", "getValidFrom", "getFromDate");
    LocalDate to   = getLocalDateIfPresent(sched, "getEndDate",   "getValidTo",   "getToDate");
    if (from != null && date.isBefore(from))
      throw new ResponseStatusException(BAD_REQUEST, "Date is before schedule start: " + from);
    if (to != null && date.isAfter(to))
      throw new ResponseStatusException(BAD_REQUEST, "Date is after schedule end: " + to);

    // 3) Group active (if StudyGroup has such flag)
    try {
      StudyGroup g = sched.getGroup();
      if (g != null) {
        Method mActive = findMethodOrNull(g.getClass(), "isActive", "getActive");
        if (mActive != null) {
          Object active = invoke(g, mActive);
          if (active instanceof Boolean b && !b)
            throw new ResponseStatusException(BAD_REQUEST, "Group is inactive");
        }
      }
    } catch (Exception e) {
      // don't block if your model doesn't expose this; keep lenient
    }
  }

  private SessionResponse toResponse(AttendanceSession s){
    long p = attendanceRepo.countBySession_IdAndStatus(s.getId(), StudentAttendanceStatus.PRESENT);
    long a = attendanceRepo.countBySession_IdAndStatus(s.getId(), StudentAttendanceStatus.ABSENT);
    SessionResponse r = new SessionResponse(
        s.getId(), s.getGroup().getId(), s.getSchedule().getId(), s.getSessionDate(),
        s.getStatus().name(), p, a
    );
    r.setPresentStudentIds(
        attendanceRepo.findStudentIdsBySessionAndStatus(s.getId(), StudentAttendanceStatus.PRESENT)
    );
    return r;
  }

  private void upsertPresent(AttendanceSession s, Student student, ZoneOffset offset) {
    var a = attendanceRepo.findBySession_IdAndStudent_Id(s.getId(), student.getId())
        .orElseGet(() -> { var na = new StudentAttendance(); na.setSession(s); na.setStudent(student); return na; });
    a.setStatus(StudentAttendanceStatus.PRESENT);
    a.setCheckedInAt(OffsetDateTime.now(offset));
    attendanceRepo.save(a);
  }

  // --------- START ----------
  @Transactional
  @Override
  public SessionResponse start(Long scheduleId, LocalDate date, ZoneOffset offset) {
    GroupSchedule sched = mustFind(GroupSchedule.class, scheduleId);
    StudyGroup group = sched.getGroup(); // ensure schedule owns the group
    assertScheduleMatchesDate(sched, date);

    // idempotent per (group, schedule, date)
    AttendanceSession s = sessionRepo.findByGroupAndScheduleAndSessionDate(group, sched, date)
        .orElseGet(() -> {
          AttendanceSession ns = new AttendanceSession();
          ns.setGroup(group);
          ns.setSchedule(sched);
          ns.setSessionDate(date);
          ns.setStartTime(sched.getStartTime());
          ns.setEndTime(sched.getEndTime());
          ns.setStatus(AttendanceSessionStatus.OPEN);
          ns.setStartedAt(OffsetDateTime.now(offset));
          return sessionRepo.save(ns);
        });

    if (s.getStatus() == AttendanceSessionStatus.CLOSED) return toResponse(s);

    // Apply pre-scans (if any) then clear them
    var pres = preRepo.findByGroup_IdAndSchedule_IdAndSessionDate(group.getId(), scheduleId, date);
    for (var pre : pres) upsertPresent(s, pre.getStudent(), offset);
    if (!pres.isEmpty()) preRepo.deleteByGroup_IdAndSchedule_IdAndSessionDate(group.getId(), scheduleId, date);

    return toResponse(s);
  }

  // --------- SCAN (single) usable before/after start ----------
  @Transactional
  @Override
  public SessionResponse scan(Long scheduleId, LocalDate date, Long studentId, ZoneOffset offset) {
    GroupSchedule sched = mustFind(GroupSchedule.class, scheduleId);
    assertScheduleMatchesDate(sched, date);
    Student student = mustFind(Student.class, studentId);

    var existing = sessionRepo.findByGroupAndScheduleAndSessionDate(sched.getGroup(), sched, date);
    if (existing.isPresent()) {
      var s = existing.get();
      if (s.getStatus() == AttendanceSessionStatus.OPEN) {
        upsertPresent(s, student, offset);
      }
      return toResponse(s); // if CLOSED, no modifications; just return summary
    }

    // Not started yet → save pre-scan
    StudentPreCheckin p = new StudentPreCheckin();
    p.setGroup(sched.getGroup());
    p.setSchedule(sched);
    p.setStudent(student);
    p.setSessionDate(date);
    p.setScannedAt(OffsetDateTime.now(offset));
    try { preRepo.save(p); } catch (Exception ignored) {}

    long count = preRepo.findByGroup_IdAndSchedule_IdAndSessionDate(
        sched.getGroup().getId(), scheduleId, date).size();
    return SessionResponse.pending(sched.getGroup().getId(), scheduleId, date, count);
  }

  // --------- SCAN (bulk) usable before/after start ----------
  @Transactional
  @Override
  public SessionResponse scanBulk(Long scheduleId, LocalDate date, List<Long> studentIds, ZoneOffset offset) {
    GroupSchedule sched = mustFind(GroupSchedule.class, scheduleId);
    assertScheduleMatchesDate(sched, date);

    var existing = sessionRepo.findByGroupAndScheduleAndSessionDate(sched.getGroup(), sched, date);
    if (existing.isPresent()) {
      var s = existing.get();
      if (s.getStatus() != AttendanceSessionStatus.OPEN) return toResponse(s);
      for (Long sid : studentIds) upsertPresent(s, mustFind(Student.class, sid), offset);
      return toResponse(s);
    }

    // Not started yet → pre-scans
    for (Long sid : studentIds) {
      StudentPreCheckin p = new StudentPreCheckin();
      p.setGroup(sched.getGroup());
      p.setSchedule(sched);
      p.setStudent(mustFind(Student.class, sid));
      p.setSessionDate(date);
      p.setScannedAt(OffsetDateTime.now(offset));
      try { preRepo.save(p); } catch (Exception ignored) {}
    }

    long count = preRepo.findByGroup_IdAndSchedule_IdAndSessionDate(
        sched.getGroup().getId(), scheduleId, date).size();
    return SessionResponse.pending(sched.getGroup().getId(), scheduleId, date, count);
  }

  // --------- teacher bulk by session (kept) ----------
  @Transactional
  @Override
  public SessionResponse bulkCheckIn(Long sessionId, List<Long> studentIds, ZoneOffset offset) {
    AttendanceSession s = mustFind(AttendanceSession.class, sessionId);
    if (s.getStatus()!=AttendanceSessionStatus.OPEN)
      throw new ResponseStatusException(BAD_REQUEST, "Session is not OPEN");
    for (Long sid : studentIds) upsertPresent(s, mustFind(Student.class, sid), offset);
    return toResponse(s);
  }

  // --------- CLOSE ----------
  @Transactional
  @Override
  public SessionResponse close(Long sessionId, ZoneOffset offset) {
    AttendanceSession s = mustFind(AttendanceSession.class, sessionId);
    if (s.getStatus()==AttendanceSessionStatus.CLOSED) return toResponse(s);

    List<Long> enrolled = studentRepo.findEnrolledStudentIds(s.getGroup().getId());
    Set<Long> present = new HashSet<>(attendanceRepo
        .findStudentIdsBySessionAndStatus(sessionId, StudentAttendanceStatus.PRESENT));

    for (Long sid : enrolled) if (!present.contains(sid)) {
      var a = attendanceRepo.findBySession_IdAndStudent_Id(sessionId, sid).orElseGet(() -> {
        var na = new StudentAttendance(); na.setSession(s); na.setStudent(mustFind(Student.class, sid)); return na; });
      a.setStatus(StudentAttendanceStatus.ABSENT);
      attendanceRepo.save(a);
    }
    s.setStatus(AttendanceSessionStatus.CLOSED);
    s.setClosedAt(OffsetDateTime.now(offset));
    sessionRepo.save(s);
    return toResponse(s);
  }

  // --------- SUMMARY (hydrate UI) ----------
  @Transactional(readOnly = true)
  @Override
  public SessionResponse summary(Long sessionId) {
    return toResponse(mustFind(AttendanceSession.class, sessionId));
  }

  // --------- MATRIX (fullName mapping) ----------
  @Transactional(readOnly = true)
  @Override
  public AttendanceMatrixResponse matrix(Long groupId, LocalDate start, LocalDate endExclusive) {
    LocalDate endInclusive = endExclusive.minusDays(1);
    if (start.isAfter(endInclusive)) {
      AttendanceMatrixResponse empty = new AttendanceMatrixResponse();
      empty.setDates(List.of());
      empty.setStudents(List.of());
      return empty;
    }

    var sessions = sessionRepo
        .findByGroup_IdAndSessionDateBetweenOrderBySessionDateAsc(groupId, start, endExclusive);

    if (sessions.isEmpty()) {
      AttendanceMatrixResponse r = new AttendanceMatrixResponse();
      r.setDates(List.of());
      r.setStudents(List.of());
      return r;
    }

    List<LocalDate> dateList = sessions.stream()
        .map(AttendanceSession::getSessionDate).distinct().sorted().toList();
    Map<LocalDate, AttendanceSession> sessionByDate = sessions.stream()
        .collect(Collectors.toMap(AttendanceSession::getSessionDate, s -> s, (a,b)->a));

    List<Long> studentIds = studentRepo.findEnrolledStudentIds(groupId);
    List<Student> students = studentRepo.findAllById(studentIds);

    List<Long> sessionIds = sessions.stream().map(AttendanceSession::getId).toList();
    var attendanceRows = attendanceRepo.findBySession_IdIn(sessionIds);

    Map<Long, Map<Long, StudentAttendanceStatus>> attMap = new HashMap<>();
    for (var a : attendanceRows) {
      attMap.computeIfAbsent(a.getSession().getId(), k -> new HashMap<>())
            .put(a.getStudent().getId(), a.getStatus());
    }

    List<String> dates = dateList.stream().map(LocalDate::toString).toList();
    List<AttendanceMatrixResponse.StudentLite> rows = new ArrayList<>();

    for (Student st : students) {
      List<String> cells = new ArrayList<>(Collections.nCopies(dateList.size(), ""));
      for (LocalDate d : dateList) {
        AttendanceSession s = sessionByDate.get(d);
        StudentAttendanceStatus stStatus =
            Optional.ofNullable(attMap.get(s.getId()))
                .map(m -> m.get(st.getId()))
                .orElse(null);
        if (stStatus == StudentAttendanceStatus.PRESENT) {
          cells.set(dates.indexOf(d.toString()), "P");
        } else if (stStatus == StudentAttendanceStatus.ABSENT) {
          cells.set(dates.indexOf(d.toString()), "A");
        } else if (s.getStatus() == AttendanceSessionStatus.CLOSED) {
          cells.set(dates.indexOf(d.toString()), "A"); // CLOSED and no row => Absent
        }
      }
      rows.add(new AttendanceMatrixResponse.StudentLite(st.getId(), safeName(st), cells));
    }

    AttendanceMatrixResponse r = new AttendanceMatrixResponse();
    r.setDates(dates);
    r.setStudents(rows);
    return r;
  }

  private String safeName(Student s) {
    try {
      var m = s.getClass().getMethod("getFullName");
      Object v = m.invoke(s);
      if (v != null) return v.toString();
    } catch (Exception ignored) {}
    return String.valueOf(s);
  }
}
