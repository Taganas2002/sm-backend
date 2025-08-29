// src/main/java/com/example/demo/dto/request/BulkCheckInRequest.java
package com.example.demo.dto.request;

import java.util.List;

public class BulkCheckInRequest {
  private Long sessionId;
  private List<Long> studentIds;

  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  public List<Long> getStudentIds() { return studentIds; }
  public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}
