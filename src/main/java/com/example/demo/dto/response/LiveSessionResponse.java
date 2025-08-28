package com.example.demo.dto.response;

public class LiveSessionResponse {
  public Long sessionId;
  public Long groupId;
  public String date;
  public String startTime;
  public String endTime;
  public String status;        // OPEN / PENDING_APPROVAL / PLANNED
  public Integer presentCount;
  public String openedAt;      // ISO-8601
}
