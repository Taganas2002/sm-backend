package com.example.demo.controllers;

import com.example.demo.models.Classroom;
import com.example.demo.models.GroupSchedule;
import com.example.demo.models.StudyGroup;
import com.example.demo.repository.ClassroomRepo;
import com.example.demo.repository.GroupScheduleRepo;
import com.example.demo.repository.StudyGroupRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class CalendarController {

  private final StudyGroupRepo groupRepo;
  private final GroupScheduleRepo scheduleRepo;
  private final ClassroomRepo classroomRepo;

  public CalendarController(StudyGroupRepo groupRepo,
                            GroupScheduleRepo scheduleRepo,
                            ClassroomRepo classroomRepo) {
    this.groupRepo = groupRepo;
    this.scheduleRepo = scheduleRepo;
    this.classroomRepo = classroomRepo;
  }

  // --------------- Expanded calendar for a group (read-only) ----------------
  @GetMapping({"/api/calendar/groups/{groupId}/events", "/calendar/groups/{groupId}"})
  public ResponseEntity<List<GroupEvent>> groupCalendar(
      @PathVariable Long groupId,
      @RequestParam String start,                 // yyyy-MM-dd
      @RequestParam String end,                   // yyyy-MM-dd (exclusive)
      @RequestParam(required = false) Integer tzOffsetMinutes
  ) {
    LocalDate startDate = LocalDate.parse(start);
    LocalDate endDate   = LocalDate.parse(end);

    ZoneOffset offset = (tzOffsetMinutes == null)
        ? OffsetDateTime.now().getOffset()
        : ZoneOffset.ofTotalSeconds(-tzOffsetMinutes * 60);

    StudyGroup group = groupRepo.findById(groupId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Group not found"));

    String groupName = computeGroupName(group);
    List<GroupSchedule> schedules = scheduleRepo.findByGroupId(groupId);

    List<GroupEvent> events = new ArrayList<>();
    for (LocalDate d = startDate; d.isBefore(endDate); d = d.plusDays(1)) {
      DayOfWeek dow = d.getDayOfWeek();
      for (GroupSchedule s : schedules) {
        if (s.isActive() && s.getDayOfWeek() == dow) {
          String classroomName = s.getClassroom() != null ? s.getClassroom().getRoomName() : null;
          GroupEvent ev = new GroupEvent();
          ev.setGroupId(groupId);
          ev.setGroupName(groupName);
          ev.setScheduleId(s.getId());
          ev.setClassroomName(classroomName);
          ev.setTitle(buildTitle(groupName, classroomName));
          ev.setStart(OffsetDateTime.of(d, s.getStartTime(), offset));
          ev.setEnd(OffsetDateTime.of(d, s.getEndTime(), offset));
          ev.setAllDay(false);
          ev.setDayOfWeek(dow.name());
          ev.setDate(d.toString());
          ev.setStartTime(s.getStartTime().toString());
          ev.setEndTime(s.getEndTime().toString());
          events.add(ev);
        }
      }
    }

    events.sort(Comparator
        .comparing(GroupEvent::getStart)
        .thenComparing(GroupEvent::getEnd)
        .thenComparing(GroupEvent::getScheduleId, Comparator.nullsLast(Long::compareTo)));

    return ResponseEntity.ok(events);
  }

  // ----------------- Availability (explicit classroom) -----------------
  @GetMapping("/api/calendar/availability/classrooms/{classroomId}")
  public ResponseEntity<AvailabilityResponse> availabilityByClassroom(
      @PathVariable Long classroomId,
      @RequestParam String dayOfWeek,
      @RequestParam String startTime,   // HH:mm
      @RequestParam String endTime,     // HH:mm
      @RequestParam(required=false) Long excludeScheduleId
  ) {
    DayOfWeek dow = resolveDay(dayOfWeek);
    LocalTime start = LocalTime.parse(startTime);
    LocalTime end   = LocalTime.parse(endTime);
    validateTimes(start, end);

    if (!classroomRepo.existsById(classroomId))
      throw new ResponseStatusException(BAD_REQUEST, "Classroom not found: " + classroomId);

    List<GroupSchedule> conflicts = scheduleRepo.findConflicts(classroomId, dow, start, end, excludeScheduleId);
    AvailabilityResponse r = new AvailabilityResponse();
    r.setClassroomId(classroomId);
    r.setDayOfWeek(dow.name());
    r.setStartTime(start.toString());
    r.setEndTime(end.toString());
    r.setAvailable(conflicts.isEmpty());
    r.setConflicts(conflicts.stream().map(this::toConflict).collect(Collectors.toList()));
    return ResponseEntity.ok(r);
  }

  // ----------------- Schedules CRUD with hard availability check -----------------

  @PostMapping("/api/calendar/groups/{groupId}/schedules")
  public ResponseEntity<ScheduleResponse> createSchedule(
      @PathVariable Long groupId,
      @RequestBody ScheduleUpsertRequest req
  ) {
    StudyGroup group = groupRepo.findById(groupId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Group not found"));

    if (req.getClassroomId() == null)
      throw new ResponseStatusException(BAD_REQUEST, "classroomId is required");

    Classroom classroom = classroomRepo.findById(req.getClassroomId())
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Classroom not found"));

    DayOfWeek dow = resolveDay(req.getDayOfWeek());
    LocalTime start = LocalTime.parse(req.getStartTime());
    LocalTime end   = LocalTime.parse(req.getEndTime());
    validateTimes(start, end);

    assertRoomFree(classroom.getId(), dow, start, end, null);

    GroupSchedule s = new GroupSchedule();
    s.setGroup(group);
    s.setClassroom(classroom);
    s.setDayOfWeek(dow);
    s.setStartTime(start);
    s.setEndTime(end);
    s.setActive(req.getActive() == null || req.getActive());

    s = scheduleRepo.save(s);
    return ResponseEntity.ok(toScheduleResponse(s));
  }

  @PutMapping("/api/calendar/schedules/{scheduleId}")
  public ResponseEntity<ScheduleResponse> updateSchedule(
      @PathVariable Long scheduleId,
      @RequestBody ScheduleUpsertRequest req
  ) {
    GroupSchedule s = scheduleRepo.findById(scheduleId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Schedule not found"));

    DayOfWeek dow   = req.getDayOfWeek() != null ? resolveDay(req.getDayOfWeek()) : s.getDayOfWeek();
    LocalTime start = req.getStartTime() != null ? LocalTime.parse(req.getStartTime()) : s.getStartTime();
    LocalTime end   = req.getEndTime() != null ? LocalTime.parse(req.getEndTime())   : s.getEndTime();
    validateTimes(start, end);

    Long classroomId = (req.getClassroomId() != null)
        ? req.getClassroomId()
        : (s.getClassroom() != null ? s.getClassroom().getId() : null);

    if (classroomId == null)
      throw new ResponseStatusException(BAD_REQUEST, "classroomId is required");

    Classroom classroom = classroomRepo.findById(classroomId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Classroom not found"));

    assertRoomFree(classroom.getId(), dow, start, end, s.getId());

    s.setClassroom(classroom);
    s.setDayOfWeek(dow);
    s.setStartTime(start);
    s.setEndTime(end);
    if (req.getActive() != null) s.setActive(req.getActive());

    s = scheduleRepo.save(s);
    return ResponseEntity.ok(toScheduleResponse(s));
  }

  @GetMapping("/api/calendar/groups/{groupId}/schedules")
  public ResponseEntity<List<ScheduleResponse>> listSchedules(@PathVariable Long groupId) {
    if (!groupRepo.existsById(groupId)) throw new ResponseStatusException(NOT_FOUND, "Group not found");
    List<GroupSchedule> list = scheduleRepo.findByGroupId(groupId);
    return ResponseEntity.ok(list.stream().map(this::toScheduleResponse).collect(Collectors.toList()));
  }

  @PatchMapping("/api/calendar/schedules/{scheduleId}/active")
  public ResponseEntity<ScheduleResponse> setActive(
      @PathVariable Long scheduleId,
      @RequestParam boolean value
  ) {
    GroupSchedule s = scheduleRepo.findById(scheduleId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Schedule not found"));
    s.setActive(value);
    s = scheduleRepo.save(s);
    return ResponseEntity.ok(toScheduleResponse(s));
  }

  @DeleteMapping("/api/calendar/schedules/{scheduleId}")
  public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
    if (!scheduleRepo.existsById(scheduleId)) throw new ResponseStatusException(NOT_FOUND, "Schedule not found");
    scheduleRepo.deleteById(scheduleId);
    return ResponseEntity.noContent().build();
  }

  // ----------------- Week calendar for ALL groups -----------------
  @GetMapping("/api/calendar/week")
  public ResponseEntity<List<GroupEvent>> weekCalendar(
      @RequestParam String weekStart,                // yyyy-MM-dd, Monday (or any day you choose)
      @RequestParam(required = false) Integer tzOffsetMinutes,
      @RequestParam(required = false) Boolean activeOnly
  ) {
    LocalDate start = LocalDate.parse(weekStart);
    LocalDate end = start.plusDays(7); // exclusive
    ZoneOffset offset = (tzOffsetMinutes == null)
        ? OffsetDateTime.now().getOffset()
        : ZoneOffset.ofTotalSeconds(-tzOffsetMinutes * 60);

    List<GroupSchedule> schedules = scheduleRepo.findAll();

    List<GroupEvent> events = new ArrayList<>();
    for (LocalDate d = start; d.isBefore(end); d = d.plusDays(1)) {
      DayOfWeek dow = d.getDayOfWeek();
      for (GroupSchedule s : schedules) {
        if (!s.isActive() || s.getDayOfWeek() != dow) continue;
        StudyGroup g = s.getGroup();
        if (activeOnly != null && activeOnly && (g == null || !g.isActive())) continue;

        String groupName = computeGroupName(g);
        String classroomName = s.getClassroom() != null ? s.getClassroom().getRoomName() : null;

        GroupEvent ev = new GroupEvent();
        ev.setGroupId(g != null ? g.getId() : null);
        ev.setGroupName(groupName);
        ev.setScheduleId(s.getId());
        ev.setClassroomName(classroomName);
        ev.setTitle(buildTitle(groupName, classroomName));
        ev.setStart(OffsetDateTime.of(d, s.getStartTime(), offset));
        ev.setEnd(OffsetDateTime.of(d, s.getEndTime(), offset));
        ev.setAllDay(false);
        ev.setDayOfWeek(dow.name());
        ev.setDate(d.toString());
        ev.setStartTime(s.getStartTime().toString());
        ev.setEndTime(s.getEndTime().toString());
        events.add(ev);
      }
    }

    events.sort(Comparator
        .comparing(GroupEvent::getStart)
        .thenComparing(GroupEvent::getGroupId, Comparator.nullsLast(Long::compareTo)));
    return ResponseEntity.ok(events);
  }

  // ----------------- Helpers -----------------
  private void assertRoomFree(Long classroomId, DayOfWeek day, LocalTime start, LocalTime end, Long excludeId) {
    List<GroupSchedule> conflicts = scheduleRepo.findConflicts(classroomId, day, start, end, excludeId);
    if (!conflicts.isEmpty()) {
      String msg = "Classroom is occupied: " + conflicts.stream()
          .map(c -> "[sched#" + c.getId() + " group#" + c.getGroup().getId() + " " +
              c.getStartTime() + "-" + c.getEndTime() + "]")
          .collect(Collectors.joining(", "));
      throw new ResponseStatusException(CONFLICT, msg);
    }
  }

  private void validateTimes(LocalTime start, LocalTime end) {
    if (!start.isBefore(end))
      throw new ResponseStatusException(BAD_REQUEST, "startTime must be before endTime");
  }

  private DayOfWeek resolveDay(String name) {
    try { return DayOfWeek.valueOf(name.toUpperCase(Locale.ROOT).trim()); }
    catch (Exception ex) { throw new ResponseStatusException(BAD_REQUEST, "Invalid dayOfWeek: " + name); }
  }

  /** Build a readable group name safely (no NPEs). */
  private String computeGroupName(StudyGroup g) {
    if (g == null) return "Group";
    String subject  = (g.getSubject()!=null && g.getSubject().getName()!=null) ? g.getSubject().getName() : null;
    String level    = (g.getLevel()!=null   && g.getLevel().getName()!=null)   ? g.getLevel().getName()   : null;
    String section  = (g.getSection()!=null && g.getSection().getName()!=null) ? g.getSection().getName() : null;
    String year     = (g.getAcademicYear()!=null && !g.getAcademicYear().isBlank()) ? g.getAcademicYear() : null;

    List<String> parts = new ArrayList<>();
    if (subject != null) parts.add(subject);
    if (level != null)   parts.add(level);
    if (section != null) parts.add(section);
    if (year != null)    parts.add(year);

    if (parts.isEmpty()) return "Group #" + g.getId();
    return String.join(" - ", parts);
  }

  private ScheduleResponse toScheduleResponse(GroupSchedule s) {
    ScheduleResponse r = new ScheduleResponse();
    r.setId(s.getId());
    if (s.getGroup() != null) {
      r.setGroupId(s.getGroup().getId());
      r.setGroupName(computeGroupName(s.getGroup()));
    }
    if (s.getClassroom() != null) {
      r.setClassroomId(s.getClassroom().getId());
      r.setClassroomName(s.getClassroom().getRoomName());
    }
    r.setDayOfWeek(s.getDayOfWeek().name());
    r.setStartTime(s.getStartTime().toString());
    r.setEndTime(s.getEndTime().toString());
    r.setActive(s.isActive());
    return r;
  }

  private Conflict toConflict(GroupSchedule s) {
    Conflict c = new Conflict();
    c.setScheduleId(s.getId());
    c.setGroupId(s.getGroup() != null ? s.getGroup().getId() : null);
    c.setDayOfWeek(s.getDayOfWeek().name());
    c.setStartTime(s.getStartTime().toString());
    c.setEndTime(s.getEndTime().toString());
    return c;
  }

  private String buildTitle(String groupName, String classroomName) {
    String room = classroomName != null ? classroomName : "Room";
    String gname = (groupName != null && !groupName.isBlank()) ? groupName : "Group";
    return gname + " • " + room;
  }

  // ----------------- DTOs -----------------

  public static class ScheduleUpsertRequest {
    private String dayOfWeek;   // "MONDAY"
    private String startTime;   // "HH:mm"
    private String endTime;     // "HH:mm"
    private Long classroomId;   // required
    private Boolean active;     // optional
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
  }

  public static class ScheduleResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long classroomId;
    private String classroomName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private boolean active;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
  }

  public static class GroupEvent {
    private Long groupId;
    private String groupName;
    private Long scheduleId;
    private String classroomName;
    private String title;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private boolean allDay;
    private String dayOfWeek;
    private String date;       // yyyy-MM-dd
    private String startTime;  // HH:mm
    private String endTime;    // HH:mm

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public OffsetDateTime getStart() { return start; }
    public void setStart(OffsetDateTime start) { this.start = start; }
    public OffsetDateTime getEnd() { return end; }
    public void setEnd(OffsetDateTime end) { this.end = end; }
    public boolean isAllDay() { return allDay; }
    public void setAllDay(boolean allDay) { this.allDay = allDay; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
  }

  public static class AvailabilityResponse {
    private Long classroomId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private boolean available;
    private List<Conflict> conflicts;
    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public List<Conflict> getConflicts() { return conflicts; }
    public void setConflicts(List<Conflict> conflicts) { this.conflicts = conflicts; }
  }

  public static class Conflict {
    private Long scheduleId;
    private Long groupId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
  }
}
