package com.example.demo.services.Implementation;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.models.*;
import com.example.demo.models.enums.ApproverType;
import com.example.demo.models.enums.SessionStatus;
import com.example.demo.repository.*;
import com.example.demo.services.Interface.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
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

  public AttendanceServiceImpl(GroupScheduleRepo scheduleRepo,
                               AttendanceSessionRepo sessionRepo,
                               StudentAttendanceRepo studentAttendanceRepo,
                               StudentRepo studentRepo,
                               TeacherRepo teacherRepo,
                               StudyGroupRepo groupRepo) {
    this.scheduleRepo = scheduleRepo;
    this.sessionRepo = sessionRepo;
    this.studentAttendanceRepo = studentAttendanceRepo;
    this.studentRepo = studentRepo;
    this.teacherRepo = teacherRepo;
    this.groupRepo = groupRepo;
  }

  @Transactional
  @Override
  public SessionSummaryDTO studentScan(StudentScanRequest req) {
    StudyGroup group = groupRepo.findById(Objects.requireNonNull(req.groupId))
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    Student student = resolveStudent(req);

    var now = LocalDateTime.now();
    var window = findActiveWindow(group.getId(), now);
    if (window == null) throw new IllegalStateException("No active schedule window right now");

    AttendanceSession session = findOrCreateSession(group, now.toLocalDate(), window.start(), window.end());

    if (!studentAttendanceRepo.existsBySessionIdAndStudentId(session.getId(), student.getId())) {
      var att = new StudentAttendance();
      att.setSession(session);
      att.setStudent(student);
      att.setCheckedInAt(OffsetDateTime.now());
      studentAttendanceRepo.save(att);

      session.setStudentScans(session.getStudentScans() + 1);
      sessionRepo.save(session);
    }

    return toSummary(session, false);
  }

  @Transactional
  @Override
  public SessionSummaryDTO teacherApprove(TeacherApproveRequest req) {
    StudyGroup group = groupRepo.findById(Objects.requireNonNull(req.groupId))
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    Teacher teacher = teacherRepo.findByCardUid(Objects.requireNonNull(req.teacherCardUid))
        .orElseThrow(() -> new IllegalArgumentException("Teacher cardUid invalid"));

    if (!Objects.equals(group.getTeacher().getId(), teacher.getId())) {
      throw new IllegalStateException("Teacher not assigned to this group");
    }

    var now = LocalDateTime.now();
    var window = findActiveWindow(group.getId(), now);
    if (window == null) throw new IllegalStateException("No active schedule window to approve");

    AttendanceSession session = findOrCreateSession(group, now.toLocalDate(), window.start(), window.end());
    if (session.getStatus() == SessionStatus.PENDING_APPROVAL || session.getStatus() == SessionStatus.PLANNED) {
      session.setApprovedBy(ApproverType.TEACHER);
      session.setApprovedAt(OffsetDateTime.now());
      session.setStatus(SessionStatus.OPEN);
      if (session.getOpenedAt() == null) session.setOpenedAt(OffsetDateTime.now());
      sessionRepo.save(session);
    }
    return toSummary(session, false);
  }

  @Transactional
  @Override
  public SessionSummaryDTO adminApprove(Long sessionId) {
    AttendanceSession session = sessionRepo.findById(Objects.requireNonNull(sessionId))
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    if (session.getStatus() == SessionStatus.PENDING_APPROVAL || session.getStatus() == SessionStatus.PLANNED) {
      session.setApprovedBy(ApproverType.ADMIN);
      session.setApprovedAt(OffsetDateTime.now());
      session.setStatus(SessionStatus.OPEN);
      if (session.getOpenedAt() == null) session.setOpenedAt(OffsetDateTime.now());
      sessionRepo.save(session);
    }
    return toSummary(session, false);
  }

  @Transactional(readOnly = true)
  @Override
  public SessionSummaryDTO liveSession(Long groupId) {
    StudyGroup group = groupRepo.findById(Objects.requireNonNull(groupId))
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    var now = LocalDateTime.now();
    var window = findActiveWindow(groupId, now);
    if (window == null) return null;

    return sessionRepo.findByGroupAndSessionDateAndStartTimeAndEndTime(
        group, now.toLocalDate(), window.start(), window.end())
        .map(s -> toSummary(s, true))
        .orElse(null);
  }

  @Transactional
  @Override
  public int autoCloseExpiredSessions() {
    int changed = 0;
    var now = LocalDateTime.now();

    var candidates = sessionRepo.findByStatusIn(List.of(SessionStatus.PENDING_APPROVAL, SessionStatus.OPEN));
    for (var s : candidates) {
      StudyGroup g = s.getGroup();
      int grace = Math.max(0, g.getAutoCloseGraceMin()); // your field on StudyGroup

      var endPlusGrace = LocalDateTime.of(s.getSessionDate(), s.getEndTime())
          .plusMinutes(grace);

      if (now.isAfter(endPlusGrace)) {
        if (s.getApprovedBy() != null) {
          s.setStatus(SessionStatus.CLOSED);
        } else {
          s.setStatus(SessionStatus.CANCELLED_UNAPPROVED);
        }
        s.setClosedAt(OffsetDateTime.now());
        sessionRepo.save(s);
        changed++;
      }
    }
    return changed;
  }

  // -------- helpers ----------
  private Student resolveStudent(StudentScanRequest req) {
    if (req.studentId != null) {
      return studentRepo.findById(req.studentId)
          .orElseThrow(() -> new IllegalArgumentException("Student not found by ID"));
    }
    if (req.studentCardUid != null && !req.studentCardUid.isBlank()) {
      return studentRepo.findByCardUid(req.studentCardUid)
          .orElseThrow(() -> new IllegalArgumentException("Student cardUid invalid"));
    }
    throw new IllegalArgumentException("Provide studentCardUid or studentId");
  }

  record Window(java.time.LocalTime start, java.time.LocalTime end) {}

  private Window findActiveWindow(Long groupId, LocalDateTime now) {
    var day = now.getDayOfWeek();
    var list = scheduleRepo.findByGroupIdAndDayOfWeekAndActiveIsTrue(groupId, day);
    var t = now.toLocalTime();
    for (var s : list) {
      if (!t.isBefore(s.getStartTime()) && !t.isAfter(s.getEndTime())) {
        return new Window(s.getStartTime(), s.getEndTime());
      }
    }
    return null;
  }

  private AttendanceSession findOrCreateSession(StudyGroup group, LocalDate date, java.time.LocalTime start, java.time.LocalTime end) {
    return sessionRepo.findByGroupAndSessionDateAndStartTimeAndEndTime(group, date, start, end)
        .orElseGet(() -> {
          var sess = new AttendanceSession();
          sess.setGroup(group);
          sess.setSessionDate(date);
          sess.setStartTime(start);
          sess.setEndTime(end);
          // default status = PENDING_APPROVAL
          return sessionRepo.save(sess);
        });
  }

  private SessionSummaryDTO toSummary(AttendanceSession s, boolean includeRoster) {
    var dto = new SessionSummaryDTO();
    dto.sessionId = s.getId();
    dto.groupId = s.getGroup().getId();
    dto.date = s.getSessionDate();
    dto.startTime = s.getStartTime();
    dto.endTime = s.getEndTime();
    dto.status = s.getStatus();
    dto.approvedBy = s.getApprovedBy() == null ? null : s.getApprovedBy().name();
    dto.studentScans = (int) studentAttendanceRepo.countBySessionId(s.getId());

    if (includeRoster) {
      var rows = studentAttendanceRepo.findBySessionId(s.getId());
      dto.students = rows.stream()
          .map(a -> new SessionStudentDTO(
              a.getStudent().getId(),
              (a.getStudent().getFullName() != null ? a.getStudent().getFullName() : "").trim(),
              a.getCheckedInAt()))
          .collect(Collectors.toList());
    }
    return dto;
  }
}
