// src/main/java/com/example/demo/dto/request/StartSessionRequest.java
package com.example.demo.dto.request;

public class StartSessionRequest {
  private Long scheduleId;    // required
  private String date;        // optional YYYY-MM-DD (defaults to today)
  public Long getScheduleId() { return scheduleId; }
  public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
}
