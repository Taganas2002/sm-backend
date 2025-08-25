package com.example.demo.dto.response;

import java.time.OffsetDateTime;

public class StudentScanResponse {
  public enum Status {
    CHECKED_IN,
    ALREADY_CHECKED_IN,
    SESSION_OPENED_AND_CHECKED_IN,
    NEED_TEACHER_APPROVAL,
    OUTSIDE_WINDOW,
    POLICY_BLOCKED,
    STUDENT_NOT_FOUND,
    GROUP_NOT_FOUND
  }

  private Status status;
  private String message;

  private Long sessionId;      // live attendance_session id (may be newly opened)
  private Long checkinId;      // attendance_checkin id when present
  private int presentCount;    // present now in session
  private OffsetDateTime sessionOpenedAt;

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  public Long getCheckinId() { return checkinId; }
  public void setCheckinId(Long checkinId) { this.checkinId = checkinId; }
  public int getPresentCount() { return presentCount; }
  public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
  public OffsetDateTime getSessionOpenedAt() { return sessionOpenedAt; }
  public void setSessionOpenedAt(OffsetDateTime sessionOpenedAt) { this.sessionOpenedAt = sessionOpenedAt; }
}
