package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentPresentRequest {
  @NotNull private Long groupId;
  @NotBlank private String slotDate;   // yyyy-MM-dd
  @NotBlank private String startTime;  // HH:mm
  @NotBlank private String endTime;    // HH:mm
  @NotBlank private String source;     // "qr" or "manual"

  private Long studentId;              // manual
  private String studentToken;         // qr

  // getters/setters
  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }
  public String getSlotDate() { return slotDate; }
  public void setSlotDate(String slotDate) { this.slotDate = slotDate; }
  public String getStartTime() { return startTime; }
  public void setStartTime(String startTime) { this.startTime = startTime; }
  public String getEndTime() { return endTime; }
  public void setEndTime(String endTime) { this.endTime = endTime; }
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }
  public Long getStudentId() { return studentId; }
  public void setStudentId(Long studentId) { this.studentId = studentId; }
  public String getStudentToken() { return studentToken; }
  public void setStudentToken(String studentToken) { this.studentToken = studentToken; }
}
