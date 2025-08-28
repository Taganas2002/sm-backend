package com.example.demo.dto.response;

public class SessionSummaryResponse {
  public Long sessionId;
  public Long groupId;
  public String date;        // yyyy-MM-dd
  public String startTime;   // HH:mm
  public String endTime;     // HH:mm
  public String status;      // OPEN/CLOSED/PLANNED
  public Integer presentCount;
  public String message;
}
