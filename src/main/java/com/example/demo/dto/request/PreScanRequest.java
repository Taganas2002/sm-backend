// src/main/java/com/example/demo/dto/request/PreScanRequest.java
package com.example.demo.dto.request;

import java.util.List;

public class PreScanRequest {
  private Long scheduleId;
  private String date;              // yyyy-MM-dd (optional -> today)
  private List<Long> studentIds;

  public Long getScheduleId() { return scheduleId; }
  public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  public List<Long> getStudentIds() { return studentIds; }
  public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}
