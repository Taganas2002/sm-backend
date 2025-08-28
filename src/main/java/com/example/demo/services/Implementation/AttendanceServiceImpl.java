package com.example.demo.services.Implementation;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.models.AttendanceSession;
import com.example.demo.models.GroupSchedule;
import com.example.demo.models.Student;
import com.example.demo.models.StudyGroup;
import com.example.demo.models.Teacher;
import com.example.demo.models.enums.ApproverType;
import com.example.demo.models.enums.AttendanceMark;
import com.example.demo.models.enums.SessionStatus;
import com.example.demo.repository.*;
import com.example.demo.services.Interface.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

  private final GroupScheduleRepo scheduleRepo;
  private final AttendanceSessionRepo sessionRepo;
  private final StudentAttendanceRepo studentAttendanceRepo;
  private final StudentRepo studentRepo;
  private final TeacherRepo teacherRepo;
  private final StudyGroupRepo groupRepo;
  private final EnrollmentRepo enrollmentRepo; // <- for roster & auto-absent

  private static final ZoneId SCHOOL_TZ = ZoneId.of("Africa/Algiers");
  private static final DateTimeFormatter DATE_F = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
  private static final DateTimeFormatter TIME_F = DateTimeFormatter.ofPattern("HH:mm");

  public AttendanceServiceImpl(GroupScheduleRepo scheduleRepo,
                               AttendanceSessionRepo sessionRepo,
                               StudentAttendanceRepo studentAttendanceRepo,
                               StudentRepo studentRepo,
                               TeacherRepo teacherRepo,
                               StudyGroupRepo groupRepo,
                               EnrollmentRepo enrollmentRepo) {
    this.scheduleRepo = scheduleRepo;
    this.sessionRepo = sessionRepo;
    this.studentAttendanceRepo = studentAttendanceRepo;
    this.studentRepo = studentRepo;
    this.teacherRepo = teacherRepo;
    this.groupRepo = groupRepo;
    this.enrollmentRepo = enrollmentRepo;
  }

  // ========================= A) Teacher starts session =========================
  @Transactional
  @Override
  public SessionSummaryResponse teacherStart(TeacherStartRequest req) {
    Objects.requireNonNull(req.getGroupId(), "groupId");
    Objects.requireNonNull(req.getSlotDate(), "slotDate");
    Objects.requireNonNull(req.getStartTime(), "startTime");
    Objects.requireNonNull(req.getEndTime(), "endTime");
    Objects.requireNonNull(req.getSource(), "source");

    StudyGroup group = groupRepo.findById(req.getGroupId())
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    // Auth: QR or manual (if manual, we assume controller already enforced auth)
    if ("qr".equalsIgnoreCase(req.getSource())) {
      String token = Objects.requireNonNull(req.getTeacherToken(), "teacherToken required for QR");
      Teacher teacher = teacherRepo.findByCardUid(token)
          .orElseThrow(() -> new IllegalArgumentException("Invalid teacher card/QR"));
      if (!Objects.equals(group.getTeacher().getId(), teacher.getId())) {
        throw new IllegalStateException("Teacher not assigned to this group");
      }
    }

    LocalDate date = LocalDate.parse(req.getSlotDate());
    LocalTime start = LocalTime.parse(req.getStartTime());
    LocalTime end   = LocalTime.parse(req.getEndTime());

    ensureSlotExistsForGroupAndDay(group.getId(), date, start, end);
    checkServerTimeWithinGrace(date, start, end);

    AttendanceSession session = sessionRepo
        .findByGroupAndSessionDateAndStartTimeAndEndTime(group, date, start, end)
        .orElseGet(() -> {
          AttendanceSession s = new AttendanceSession();
          s.setGroup(group);
          s.setSessionDate(date);
          s.setStartTime(start);
          s.setEndTime(end);
          s.setStatus(SessionStatus.PLANNED);
          return sessionRepo.save(s);
        });

    if (session.getStatus() == SessionStatus.PLANNED || session.getStatus() == SessionStatus.PENDING_APPROVAL) {
      session.setStatus(SessionStatus.OPEN);
      session.setApprovedBy(ApproverType.TEACHER);
      session.setApprovedAt(OffsetDateTime.now(SCHOOL_TZ));
      if (session.getOpenedAt() == null) session.setOpenedAt(OffsetDateTime.now(SCHOOL_TZ));
      sessionRepo.save(session);
    }

    return toSummaryResponse(session, "Session opened.");
  }

  // ========================= B) Student present (slot identity) =========================
  @Transactional
  @Override
  public StudentPresentResponse studentPresent(StudentPresentRequest req) {
    Objects.requireNonNull(req.getGroupId(), "groupId");
    Objects.requireNonNull(req.getSlotDate(), "slotDate");
    Objects.requireNonNull(req.getStartTime(), "startTime");
    Objects.requireNonNull(req.getEndTime(), "endTime");
    Objects.requireNonNull(req.getSource(), "source");

    StudyGroup group = groupRepo.findById(req.getGroupId())
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    Student student = resolveStudent(req.getStudentId(), req.getStudentToken());

    LocalDate date = LocalDate.parse(req.getSlotDate());
    LocalTime start = LocalTime.parse(req.getStartTime());
    LocalTime end   = LocalTime.parse(req.getEndTime());

    ensureSlotExistsForGroupAndDay(group.getId(), date, start, end);

    Optional<AttendanceSession> opt = sessionRepo
        .findByGroupAndSessionDateAndStartTimeAndEndTime(group, date, start, end);

    if (opt.isEmpty() || opt.get().getStatus() != SessionStatus.OPEN) {
      StudentPresentResponse r = new StudentPresentResponse();
      r.status = "WAITING_FOR_TEACHER";
      r.slotDate = date.format(DATE_F);
      r.startTime = start.format(TIME_F);
      r.endTime = end.format(TIME_F);
      r.message = "Teacher has not started the session yet.";
      return r;
    }

    AttendanceSession session = opt.get();
    boolean already = studentAttendanceRepo.existsBySessionIdAndStudentId(session.getId(), student.getId());
    if (!already) {
      var att = new com.example.demo.models.StudentAttendance();
      att.setSession(session);
      att.setStudent(student);
      att.setStatus(AttendanceMark.PRESENT);
      att.setCheckedInAt(OffsetDateTime.now(SCHOOL_TZ));
      studentAttendanceRepo.save(att);
    }

    StudentPresentResponse r = new StudentPresentResponse();
    r.status = already ? "ALREADY_CHECKED_IN" : "CHECKED_IN";
    r.sessionId = session.getId();
    r.presentCount = countPresent(session.getId());
    r.message = already ? "Already checked in." : "Checked in.";
    return r;
  }

  // ========================= B2) Student present (by sessionId) =========================
  @Transactional
  @Override
  public StudentPresentResponse studentPresentBySessionId(Long sessionId, Long studentId, String studentToken, String source) {
    AttendanceSession session = sessionRepo.findById(Objects.requireNonNull(sessionId))
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));

    if (session.getStatus() != SessionStatus.OPEN) {
      StudentPresentResponse r = new StudentPresentResponse();
      r.status = "WAITING_FOR_TEACHER";
      r.slotDate = session.getSessionDate().format(DATE_F);
      r.startTime = session.getStartTime().format(TIME_F);
      r.endTime = session.getEndTime().format(TIME_F);
      r.message = "Session is not OPEN.";
      return r;
    }

    Student student = resolveStudent(studentId, studentToken);

    boolean already = studentAttendanceRepo.existsBySessionIdAndStudentId(session.getId(), student.getId());
    if (!already) {
      var att = new com.example.demo.models.StudentAttendance();
      att.setSession(session);
      att.setStudent(student);
      att.setStatus(AttendanceMark.PRESENT);
      att.setCheckedInAt(OffsetDateTime.now(SCHOOL_TZ));
      studentAttendanceRepo.save(att);
    }

    StudentPresentResponse r = new StudentPresentResponse();
    r.status = already ? "ALREADY_CHECKED_IN" : "CHECKED_IN";
    r.sessionId = session.getId();
    r.presentCount = countPresent(session.getId());
    r.message = already ? "Already checked in." : "Checked in.";
    return r;
  }

  // ========================= C) Bulk present (slot identity) =========================
  @Transactional
  @Override
  public BulkPresentResponse bulkPresent(BulkPresentRequest req) {
    Objects.requireNonNull(req.getGroupId(), "groupId");
    Objects.requireNonNull(req.getSlotDate(), "slotDate");
    Objects.requireNonNull(req.getStartTime(), "startTime");
    Objects.requireNonNull(req.getEndTime(), "endTime");
    Objects.requireNonNull(req.getSource(), "source");
    Objects.requireNonNull(req.getStudentIds(), "studentIds");

    StudyGroup group = groupRepo.findById(req.getGroupId())
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    LocalDate date = LocalDate.parse(req.getSlotDate());
    LocalTime start = LocalTime.parse(req.getStartTime());
    LocalTime end   = LocalTime.parse(req.getEndTime());

    ensureSlotExistsForGroupAndDay(group.getId(), date, start, end);

    AttendanceSession session = sessionRepo
        .findByGroupAndSessionDateAndStartTimeAndEndTime(group, date, start, end)
        .orElseThrow(() -> new IllegalStateException("Session is not opened yet (teacher must start)"));

    if (session.getStatus() != SessionStatus.OPEN) {
      throw new IllegalStateException("Session is not OPEN");
    }

    int marked = req.getStudentIds().size();
    int newly = 0;

    for (Long sid : req.getStudentIds()) {
      Student st = studentRepo.findById(sid)
          .orElseThrow(() -> new IllegalArgumentException("Student not found: " + sid));
      boolean already = studentAttendanceRepo.existsBySessionIdAndStudentId(session.getId(), st.getId());
      if (!already) {
        var att = new com.example.demo.models.StudentAttendance();
        att.setSession(session);
        att.setStudent(st);
        att.setStatus(AttendanceMark.PRESENT);
        att.setCheckedInAt(OffsetDateTime.now(SCHOOL_TZ));
        studentAttendanceRepo.save(att);
        newly++;
      }
    }

    BulkPresentResponse r = new BulkPresentResponse();
    r.sessionId = session.getId();
    r.groupId = group.getId();
    r.marked = marked;
    r.newlyAdded = newly;
    r.presentCount = countPresent(session.getId());
    return r;
  }

  // ========================= C2) Bulk present (by sessionId) =========================
  @Transactional
  @Override
  public BulkPresentResponse bulkPresentBySessionId(Long sessionId, List<Long> studentIds, String source) {
    AttendanceSession session = sessionRepo.findById(Objects.requireNonNull(sessionId))
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));

    if (session.getStatus() != SessionStatus.OPEN) {
      throw new IllegalStateException("Session is not OPEN");
    }

    int marked = studentIds.size();
    int newly = 0;

    for (Long sid : studentIds) {
      Student st = studentRepo.findById(sid)
          .orElseThrow(() -> new IllegalArgumentException("Student not found: " + sid));
      boolean already = studentAttendanceRepo.existsBySessionIdAndStudentId(session.getId(), st.getId());
      if (!already) {
        var att = new com.example.demo.models.StudentAttendance();
        att.setSession(session);
        att.setStudent(st);
        att.setStatus(AttendanceMark.PRESENT);
        att.setCheckedInAt(OffsetDateTime.now(SCHOOL_TZ));
        studentAttendanceRepo.save(att);
        newly++;
      }
    }

    BulkPresentResponse r = new BulkPresentResponse();
    r.sessionId = session.getId();
    r.groupId = session.getGroup().getId();
    r.marked = marked;
    r.newlyAdded = newly;
    r.presentCount = countPresent(session.getId());
    return r;
  }

  // ========================= D) Close session (auto-ABSENT fill) ======================
  @Transactional
  @Override
  public CloseSessionResponse closeSession(Long sessionId, String reason) {
    AttendanceSession s = sessionRepo.findById(Objects.requireNonNull(sessionId))
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));

    boolean applied = false;

    if (s.getStatus() != SessionStatus.CLOSED) {
      // fill ABSENT for enrolled students who don't have a row yet
      applied = fillAbsentees(s);
      s.setStatus(SessionStatus.CLOSED);
      s.setClosedAt(OffsetDateTime.now(SCHOOL_TZ));
      sessionRepo.save(s);
    }

    CloseSessionResponse r = new CloseSessionResponse();
    r.ok = true;
    r.status = s.getStatus().name();
    r.autoAbsentApplied = applied;
    return r;
  }

  // ========================= E) Live session =========================
  @Transactional(readOnly = true)
  @Override
  public LiveSessionResponse liveSession(Long groupId) {
    StudyGroup group = groupRepo.findById(Objects.requireNonNull(groupId))
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    ZonedDateTime now = ZonedDateTime.now(SCHOOL_TZ);
    DayOfWeek dow = now.getDayOfWeek();
    LocalTime t = now.toLocalTime();

    List<GroupSchedule> slots = scheduleRepo.findByGroupIdAndDayOfWeekAndActiveIsTrue(groupId, dow);
    for (GroupSchedule s : slots) {
      if (!t.isBefore(s.getStartTime()) && !t.isAfter(s.getEndTime())) {
        Optional<AttendanceSession> opt = sessionRepo
            .findByGroupAndSessionDateAndStartTimeAndEndTime(group, now.toLocalDate(), s.getStartTime(), s.getEndTime());
        if (opt.isPresent()) {
          AttendanceSession as = opt.get();
          LiveSessionResponse r = new LiveSessionResponse();
          r.sessionId = as.getId();
          r.groupId = groupId;
          r.date = as.getSessionDate().format(DATE_F);
          r.startTime = as.getStartTime().format(TIME_F);
          r.endTime = as.getEndTime().format(TIME_F);
          r.status = as.getStatus().name();
          r.presentCount = countPresent(as.getId());
          r.openedAt = as.getOpenedAt() != null ? as.getOpenedAt().toString() : null;
          return r;
        } else {
          return null; // no materialized session yet
        }
      }
    }
    return null;
  }

  // ========================= F) Sessions list & detail =========================
  @Transactional(readOnly = true)
  @Override
  public SessionsListResponse listSessions(Long groupId, LocalDate from, LocalDate to, boolean includePlanned) {
    StudyGroup group = groupRepo.findById(Objects.requireNonNull(groupId))
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    List<AttendanceSession> sessions =
        sessionRepo.findByGroupIdAndSessionDateBetweenOrderBySessionDateAscStartTimeAsc(groupId, from, to);

    Map<String, AttendanceSession> byKey = new HashMap<>();
    for (AttendanceSession s : sessions) {
      byKey.put(key(s.getSessionDate(), s.getStartTime(), s.getEndTime()), s);
    }

    List<SessionsListResponse.Item> out = new ArrayList<>();

    for (AttendanceSession s : sessions) {
      SessionsListResponse.Item it = new SessionsListResponse.Item();
      it.sessionId = s.getId();
      it.date = s.getSessionDate().format(DATE_F);
      it.startTime = s.getStartTime().format(TIME_F);
      it.endTime = s.getEndTime().format(TIME_F);
      it.status = s.getStatus().name();
      it.presentCount = countPresent(s.getId());
      out.add(it);
    }

    if (includePlanned) {
      Map<DayOfWeek, List<GroupSchedule>> schedByDay = scheduleRepo.findByGroupId(groupId)
          .stream().filter(GroupSchedule::isActive).collect(Collectors.groupingBy(GroupSchedule::getDayOfWeek));

      LocalDate d = from;
      while (!d.isAfter(to)) {
        DayOfWeek dow = d.getDayOfWeek();
        List<GroupSchedule> daySlots = schedByDay.getOrDefault(dow, Collections.emptyList());
        for (GroupSchedule gs : daySlots) {
          String k = key(d, gs.getStartTime(), gs.getEndTime());
          if (!byKey.containsKey(k)) {
            SessionsListResponse.Item it = new SessionsListResponse.Item();
            it.sessionId = null;
            it.date = d.format(DATE_F);
            it.startTime = gs.getStartTime().format(TIME_F);
            it.endTime = gs.getEndTime().format(TIME_F);
            it.status = SessionStatus.PLANNED.name();
            it.presentCount = null;
            out.add(it);
          }
        }
        d = d.plusDays(1);
      }

      out.sort(Comparator
          .comparing((SessionsListResponse.Item i) -> LocalDate.parse(i.date))
          .thenComparing(i -> LocalTime.parse(i.startTime)));
    }

    SessionsListResponse r = new SessionsListResponse();
    r.items = out;
    return r;
  }

  @Transactional(readOnly = true)
  @Override
  public SessionDetailResponse getSession(Long sessionId) {
    AttendanceSession s = sessionRepo.findById(Objects.requireNonNull(sessionId))
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));

    SessionDetailResponse r = new SessionDetailResponse();
    r.sessionId = s.getId();
    r.groupId = s.getGroup().getId();
    r.date = s.getSessionDate().format(DATE_F);
    r.startTime = s.getStartTime().format(TIME_F);
    r.endTime = s.getEndTime().format(TIME_F);
    r.status = s.getStatus().name();

    var rows = studentAttendanceRepo.findBySessionId(s.getId());
    r.students = rows.stream().map(a -> {
      SessionDetailResponse.StudentRow row = new SessionDetailResponse.StudentRow();
      row.studentId = a.getStudent().getId();
      String nm = a.getStudent().getFullName();
      row.name = nm == null ? "" : nm;
      row.checkedInAt = a.getCheckedInAt() == null ? null : a.getCheckedInAt().toString();
      row.attendanceStatus = (a.getStatus() == AttendanceMark.PRESENT) ? "PRESENT"
          : (a.getCheckedInAt() != null ? "PRESENT" : "ABSENT");
      return row;
    }).collect(Collectors.toList());

    return r;
  }

  // ========================= G) Running consumption (X/quota) =========================
  @Transactional(readOnly = true)
  @Override
  public ConsumptionRunningResponse runningConsumption(Long studentId, Long groupId) {
    Student st = studentRepo.findById(Objects.requireNonNull(studentId))
        .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    StudyGroup group = groupRepo.findById(Objects.requireNonNull(groupId))
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    long attended = studentAttendanceRepo.countByStudentIdAndSession_Group_Id(studentId, groupId);

    int quota = Optional.ofNullable(group.getSessionsPerMonth()).orElse(8);
    int attendedInt = (int) attended;

    int cyclesCompleted = (quota > 0) ? (attendedInt / quota) : 0;
    int currentCycleAttended = (quota > 0) ? (attendedInt % quota) : attendedInt;
    int remaining = (quota > 0) ? (quota - currentCycleAttended) : 0;
    boolean needsPayment = quota > 0 && attendedInt > 0 && attendedInt % quota == 0;

    ConsumptionRunningResponse r = new ConsumptionRunningResponse();
    r.studentId = studentId;
    r.groupId = groupId;
    r.attended = attendedInt;
    r.quota = quota;
    r.ratio = quota > 0 ? (currentCycleAttended + "/" + quota) : (attendedInt + "/∞");
    r.cyclesCompleted = cyclesCompleted;
    r.currentCycleAttended = currentCycleAttended;
    r.remainingInCurrentCycle = remaining;
    r.needsPayment = needsPayment;
    return r;
  }

  // ========================= Utility =========================
  @Transactional
  @Override
  public Long ensureSessionId(Long groupId, LocalDate slotDate, LocalTime startTime, LocalTime endTime, boolean openIfTeacherStart) {
    StudyGroup group = groupRepo.findById(Objects.requireNonNull(groupId))
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    ensureSlotExistsForGroupAndDay(group.getId(), slotDate, startTime, endTime);

    AttendanceSession session = sessionRepo
        .findByGroupAndSessionDateAndStartTimeAndEndTime(group, slotDate, startTime, endTime)
        .orElseGet(() -> {
          AttendanceSession s = new AttendanceSession();
          s.setGroup(group);
          s.setSessionDate(slotDate);
          s.setStartTime(startTime);
          s.setEndTime(endTime);
          s.setStatus(SessionStatus.PLANNED);
          return sessionRepo.save(s);
        });

    if (openIfTeacherStart && session.getStatus() != SessionStatus.OPEN) {
      session.setStatus(SessionStatus.OPEN);
      session.setApprovedBy(ApproverType.TEACHER);
      session.setApprovedAt(OffsetDateTime.now(SCHOOL_TZ));
      if (session.getOpenedAt() == null) session.setOpenedAt(OffsetDateTime.now(SCHOOL_TZ));
      sessionRepo.save(session);
    }

    return session.getId();
  }

  // ====== Roster: set single cell (Present / Absent) ======
  @Override
  @Transactional
  public SessionDetailResponse setRosterMark(Long sessionId, Long studentId, String status, String source) {
    var s = sessionRepo.findById(Objects.requireNonNull(sessionId))
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    var st = studentRepo.findById(Objects.requireNonNull(studentId))
        .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    AttendanceMark mark = parseMark(status);

    var rowOpt = studentAttendanceRepo.findBySessionIdAndStudentId(sessionId, studentId);
    com.example.demo.models.StudentAttendance row = rowOpt.orElseGet(() -> {
      var a = new com.example.demo.models.StudentAttendance();
      a.setSession(s);
      a.setStudent(st);
      return a;
    });

    row.setStatus(mark);
    if (mark == AttendanceMark.PRESENT) {
      if (row.getCheckedInAt() == null) {
        row.setCheckedInAt(OffsetDateTime.now(SCHOOL_TZ));
      }
    } else {
      row.setCheckedInAt(null);
    }

    studentAttendanceRepo.save(row);
    return getSession(sessionId);
  }

  // ========================= Helpers =========================
  private void ensureSlotExistsForGroupAndDay(Long groupId, LocalDate date, LocalTime start, LocalTime end) {
    DayOfWeek dow = date.getDayOfWeek();
    List<GroupSchedule> slots = scheduleRepo.findByGroupIdAndDayOfWeekAndActiveIsTrue(groupId, dow);
    boolean exists = slots.stream().anyMatch(s ->
        start.equals(s.getStartTime()) && end.equals(s.getEndTime())
    );
    if (!exists) {
      throw new IllegalArgumentException("No schedule slot for this group at the given date/time");
    }
  }

  private void checkServerTimeWithinGrace(LocalDate date, LocalTime start, LocalTime end) {
    ZonedDateTime now = ZonedDateTime.now(SCHOOL_TZ);
    if (!now.toLocalDate().equals(date)) return;
    LocalDateTime slotStart = LocalDateTime.of(date, start).minusMinutes(15);
    LocalDateTime slotEnd   = LocalDateTime.of(date, end).plusMinutes(15);
    LocalDateTime probe = now.toLocalDateTime();
    if (probe.isBefore(slotStart) || probe.isAfter(slotEnd)) {
      // not fatal in MVP
    }
  }

  private Student resolveStudent(Long studentId, String studentToken) {
    if (studentId != null) {
      return studentRepo.findById(studentId)
          .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }
    if (studentToken != null && !studentToken.isBlank()) {
      return studentRepo.findByCardUid(studentToken)
          .orElseThrow(() -> new IllegalArgumentException("Invalid student card/QR"));
    }
    throw new IllegalArgumentException("Provide studentId or studentToken");
  }

  private SessionSummaryResponse toSummaryResponse(AttendanceSession s, String msg) {
    SessionSummaryResponse r = new SessionSummaryResponse();
    r.sessionId = s.getId();
    r.groupId = s.getGroup().getId();
    r.date = s.getSessionDate().format(DATE_F);
    r.startTime = s.getStartTime().format(TIME_F);
    r.endTime = s.getEndTime().format(TIME_F);
    r.status = s.getStatus().name();
    r.presentCount = countPresent(s.getId());
    r.message = msg;
    return r;
  }

  private static String key(LocalDate d, LocalTime s, LocalTime e) {
    return d + "|" + s + "|" + e;
  }

  /** Count PRESENT rows. Falls back to in-memory check if repo doesn't support by-status. */
  private int countPresent(Long sessionId) {
    try {
      // if you added this method, it will be used
      return (int) studentAttendanceRepo.countBySessionIdAndStatus(sessionId, AttendanceMark.PRESENT);
    } catch (Throwable ignore) {
      return (int) studentAttendanceRepo.findBySessionId(sessionId).stream()
          .mapToInt(a -> {
            try {
              return (a.getStatus() == AttendanceMark.PRESENT) ? 1
                  : (a.getCheckedInAt() != null ? 1 : 0);
            } catch (Throwable e) {
              return (a.getCheckedInAt() != null ? 1 : 0);
            }
          }).sum();
    }
  }

  /** Insert ABSENT rows for enrolled students without a row. Returns true if any were inserted. */
  private boolean fillAbsentees(AttendanceSession s) {
    List<Student> enrolled = enrollmentRepo
        .findActiveStudentsOnDate(s.getGroup().getId(), s.getSessionDate());
    Set<Long> alreadyRow = studentAttendanceRepo.findBySessionId(s.getId())
        .stream().map(a -> a.getStudent().getId()).collect(Collectors.toSet());

    boolean any = false;
    for (Student st : enrolled) {
      if (!alreadyRow.contains(st.getId())) {
        var a = new com.example.demo.models.StudentAttendance();
        a.setSession(s);
        a.setStudent(st);
        a.setStatus(AttendanceMark.ABSENT);
        a.setCheckedInAt(null);
        studentAttendanceRepo.save(a);
        any = true;
      }
    }
    return any;
  }

  private AttendanceMark parseMark(String s) {
    if (s == null) return AttendanceMark.ABSENT;
    String x = s.trim().toUpperCase();
    if ("PRESENT".equals(x) || "P".equals(x) || "CHECKED_IN".equals(x)) return AttendanceMark.PRESENT;
    return AttendanceMark.ABSENT;
  }
}
