package com.example.demo.controllers;

import com.example.demo.dto.request.BulkCheckInRequest;
import com.example.demo.dto.request.BulkIds;
import com.example.demo.dto.response.AttendanceMatrixResponse;
import com.example.demo.dto.response.SessionResponse;
import com.example.demo.services.Interface.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

  private final AttendanceService service;

  public AttendanceController(AttendanceService service) { this.service = service; }

  private ZoneOffset toOffset(Integer tzOffsetMinutes) {
    return (tzOffsetMinutes == null)
        ? OffsetDateTime.now().getOffset()
        : ZoneOffset.ofTotalSeconds(-tzOffsetMinutes * 60);
  }

  // 1) Teacher START — require date to disambiguate weekly schedule
  @PostMapping("/teacher/start")
  public ResponseEntity<SessionResponse> start(
      @RequestParam Long scheduleId,
      @RequestParam String date,                 // yyyy-MM-dd REQUIRED
      @RequestParam(required=false) Integer tzOffsetMinutes
  ) {
    return ResponseEntity.ok(service.start(scheduleId, LocalDate.parse(date), toOffset(tzOffsetMinutes)));
  }

  // 2a) SCAN (single) — same endpoint before/after start
  @PostMapping("/scan")
  public ResponseEntity<SessionResponse> scan(
      @RequestParam Long scheduleId,
      @RequestParam Long studentId,
      @RequestParam String date,                 // yyyy-MM-dd REQUIRED
      @RequestParam(required=false) Integer tzOffsetMinutes
  ) {
    return ResponseEntity.ok(service.scan(scheduleId, LocalDate.parse(date), studentId, toOffset(tzOffsetMinutes)));
  }

  // 2b) SCAN (bulk) — same endpoint before/after start
  @PostMapping("/scan/bulk")
  public ResponseEntity<SessionResponse> scanBulk(
      @RequestParam Long scheduleId,
      @RequestParam String date,
      @RequestParam(required=false) Integer tzOffsetMinutes,
      @RequestBody BulkIds body
  ) {
    return ResponseEntity.ok(service.scanBulk(scheduleId, LocalDate.parse(date), body.getStudentIds(), toOffset(tzOffsetMinutes)));
  }

  // (optional) TA bulk present by sessionId – keep for efficiency
  @PostMapping("/students/checkin")
  public ResponseEntity<SessionResponse> bulkCheckinBySession(
      @RequestBody BulkCheckInRequest req,
      @RequestParam(required=false) Integer tzOffsetMinutes
  ) {
    return ResponseEntity.ok(service.bulkCheckIn(req.getSessionId(), req.getStudentIds(), toOffset(tzOffsetMinutes)));
  }

  // 3) Close
  @PostMapping("/teacher/close")
  public ResponseEntity<SessionResponse> close(
      @RequestParam Long sessionId,
      @RequestParam(required=false) Integer tzOffsetMinutes
  ) {
    return ResponseEntity.ok(service.close(sessionId, toOffset(tzOffsetMinutes)));
  }

  // 4) Matrix
  @GetMapping("/matrix")
  public ResponseEntity<AttendanceMatrixResponse> matrix(
      @RequestParam Long groupId,
      @RequestParam String start,
      @RequestParam String endExclusive
  ) {
    return ResponseEntity.ok(service.matrix(groupId, LocalDate.parse(start), LocalDate.parse(endExclusive)));
  }

  // 5) Hydrate checkboxes on reload
  @GetMapping("/session/{sessionId}/summary")
  public ResponseEntity<SessionResponse> summary(@PathVariable Long sessionId) {
    return ResponseEntity.ok(service.summary(sessionId));
  }
}
