package com.example.demo.services.Interface;

import com.example.demo.dto.request.AdminApproveRequest;
import com.example.demo.dto.request.StudentScanRequest;
import com.example.demo.dto.request.TeacherApproveRequest;
import com.example.demo.dto.response.SessionSummaryDTO;

public interface AttendanceService {
  SessionSummaryDTO studentScan(StudentScanRequest req);
  SessionSummaryDTO teacherApprove(TeacherApproveRequest req);
  SessionSummaryDTO adminApprove(Long sessionId);
  SessionSummaryDTO liveSession(Long groupId);
  int autoCloseExpiredSessions();
}
