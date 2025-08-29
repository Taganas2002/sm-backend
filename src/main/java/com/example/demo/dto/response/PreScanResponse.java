// src/main/java/com/example/demo/dto/response/PreScanResponse.java
package com.example.demo.dto.response;

public class PreScanResponse {
  private Long groupId;
  private Long scheduleId;
  private String date;   // yyyy-MM-dd
  private int accepted;

  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }
  public Long getScheduleId() { return scheduleId; }
  public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  public int getAccepted() { return accepted; }
  public void setAccepted(int accepted) { this.accepted = accepted; }
}
