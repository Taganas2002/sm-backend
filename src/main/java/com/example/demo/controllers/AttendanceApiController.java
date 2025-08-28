package com.example.demo.controllers;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.services.Interface.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceApiController {

  private final AttendanceService service;
  public AttendanceApiController(AttendanceService service){ this.service = service; }

  // ---------------- A) Teacher starts a session (QR or manual) ----------------
  @PostMapping("/teachers/start")
  public SessionSummaryResponse teacherStart(@RequestBody @Valid TeacherStartRequest req) {
    return service.teacherStart(req);
  }

  // ---------------- B) Student present (QR or manual) by slot identity --------
  @PostMapping("/students/present")
  public StudentPresentResponse studentPresent(@RequestBody @Valid StudentPresentRequest req) {
    return service.studentPresent(req);
  }

  // -------- B2) Student present by sessionId (when UI knows sessionId) --------
  @PostMapping("/sessions/{sessionId}/present")
  public StudentPresentResponse studentPresentBySession(@PathVariable Long sessionId,
                                                        @RequestParam(required = false) Long studentId,
                                                        @RequestParam(required = false) String studentToken,
                                                        @RequestParam(defaultValue = "manual") String source) {
    return service.studentPresentBySessionId(sessionId, studentId, studentToken, source);
  }

  // ---------------- C) Bulk present (manual) by slot identity -----------------
  @PostMapping("/students/bulk-present")
  public BulkPresentResponse bulkPresent(@RequestBody @Valid BulkPresentRequest req) {
    return service.bulkPresent(req);
  }

  // -------------- C2) Bulk present by sessionId (when UI knows it) ------------
  @PostMapping("/sessions/{sessionId}/bulk-present")
  public BulkPresentResponse bulkPresentBySession(@PathVariable Long sessionId,
                                                  @RequestBody List<Long> studentIds,
                                                  @RequestParam(defaultValue = "manual") String source) {
    return service.bulkPresentBySessionId(sessionId, studentIds, source);
  }

  // ---------------- D) Close session (auto-ABSENT in service) -----------------
  @PostMapping("/sessions/{sessionId}/close")
  public CloseSessionResponse close(@PathVariable Long sessionId,
                                    @RequestBody(required = false) CloseSessionRequest body) {
    String reason = (body == null ? null : body.getReason());
    return service.closeSession(sessionId, reason);
  }

  // ---------------- E) Live session (if opened now) ---------------------------
  @GetMapping("/sessions/live")
  public LiveSessionResponse live(@RequestParam Long groupId) {
    return service.liveSession(groupId);
  }

  // ---------------- F) Sessions list (range; include planned) -----------------
  @GetMapping("/sessions")
  public SessionsListResponse list(@RequestParam Long groupId,
                                   @RequestParam String from,
                                   @RequestParam String to,
                                   @RequestParam(defaultValue = "false") boolean includePlanned) {
    return service.listSessions(groupId, LocalDate.parse(from), LocalDate.parse(to), includePlanned);
  }

  // ---------------- F2) Session detail ----------------------------------------
  @GetMapping("/sessions/{sessionId}")
  public SessionDetailResponse detail(@PathVariable Long sessionId) {
    return service.getSession(sessionId);
  }

  // ---------------- G) Running consumption (X / quota) ------------------------
  @GetMapping("/consumption/students/{studentId}/groups/{groupId}/running")
  public ConsumptionRunningResponse running(@PathVariable Long studentId, @PathVariable Long groupId) {
    return service.runningConsumption(studentId, groupId);
  }

  // ---------------- Utility: ensure session id for a slot ---------------------
  @PostMapping("/slots/ensure-session")
  public Map<String,Object> ensureSession(@RequestParam Long groupId,
                            @RequestParam String slotDate,
                            @RequestParam String startTime,
                            @RequestParam String endTime,
                            @RequestParam(defaultValue = "false") boolean openIfTeacherStart) {
    Long id = service.ensureSessionId(
        groupId,
        LocalDate.parse(slotDate),
        LocalTime.parse(startTime),
        LocalTime.parse(endTime),
        openIfTeacherStart
    );
    return Map.of("sessionId", id);
  }

  // ---------------- Nice JSON error mapping -----------------------------------
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public Map<String, Object> onBadRequest(IllegalArgumentException ex) {
    return Map.of("errorCode", "BAD_REQUEST", "message", ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> onIllegalState(IllegalStateException ex) {
    if ("NOT_ENROLLED".equals(ex.getMessage())) {
      return Map.of("errorCode", "NOT_ENROLLED", "message", "Student is not enrolled in this group for the slot date.");
    }
    return Map.of("errorCode", "CONFLICT", "message", ex.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Map<String, Object> onOther(Exception ex) {
    return Map.of("errorCode", "RUNTIME_EXCEPTION", "message", ex.getMessage());
  }
}
