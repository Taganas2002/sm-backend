package com.example.demo.dto.response;

import java.time.OffsetDateTime;

public class TeacherApproveResponse {
  private Long sessionId;
  private Long groupId;
  private boolean approved;
  private String message;
  private OffsetDateTime openedAt;

  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }
  public boolean isApproved() { return approved; }
  public void setApproved(boolean approved) { this.approved = approved; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public OffsetDateTime getOpenedAt() { return openedAt; }
  public void setOpenedAt(OffsetDateTime openedAt) { this.openedAt = openedAt; }
}
