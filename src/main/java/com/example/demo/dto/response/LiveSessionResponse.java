package com.example.demo.dto.response;

import java.time.OffsetDateTime;

public class LiveSessionResponse {
  private Long sessionId;
  private Long groupId;
  private String state;           // OPEN or PENDING_APPROVAL (in Mode B)
  private int presentCount;
  private OffsetDateTime openedAt;

  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }
  public String getState() { return state; }
  public void setState(String state) { this.state = state; }
  public int getPresentCount() { return presentCount; }
  public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
  public OffsetDateTime getOpenedAt() { return openedAt; }
  public void setOpenedAt(OffsetDateTime openedAt) { this.openedAt = openedAt; }
}
