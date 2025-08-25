package com.example.demo.controllers;

import com.example.demo.dto.request.*;
import com.example.demo.services.Interface.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

  private final AttendanceService attendanceService;

  public AttendanceController(AttendanceService attendanceService) {
    this.attendanceService = attendanceService;
  }

  @PostMapping("/scan")
  public ResponseEntity<?> scan(@RequestBody StudentScanRequest req) {
    return ResponseEntity.ok(attendanceService.studentScan(req));
  }

  @PostMapping("/sessions/approve-by-teacher")
  public ResponseEntity<?> approveByTeacher(@RequestBody TeacherApproveRequest req) {
    return ResponseEntity.ok(attendanceService.teacherApprove(req));
  }

  @PostMapping("/sessions/{sessionId}/approve-by-admin")
  public ResponseEntity<?> approveByAdmin(@PathVariable Long sessionId, @RequestBody AdminApproveRequest req) {
    if (!req.approve) return ResponseEntity.badRequest().body("approve=false not supported");
    return ResponseEntity.ok(attendanceService.adminApprove(sessionId));
  }

  @GetMapping("/sessions/live")
  public ResponseEntity<?> live(@RequestParam Long groupId) {
    return ResponseEntity.ok(attendanceService.liveSession(groupId));
  }
}
