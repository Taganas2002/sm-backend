package com.example.demo.dto.response;

public class AdminApproveResponse {
  private Long sessionId;
  private boolean approved;
  private String message;

  public static AdminApproveResponse failure(String msg) {
    AdminApproveResponse r = new AdminApproveResponse();
    r.approved = false; r.message = msg; return r;
  }

  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  public boolean isApproved() { return approved; }
  public void setApproved(boolean approved) { this.approved = approved; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
}
