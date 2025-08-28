package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BulkPresentRequest {
  @NotNull private Long groupId;
  @NotBlank private String slotDate;   // yyyy-MM-dd
  @NotBlank private String startTime;  // HH:mm
  @NotBlank private String endTime;    // HH:mm
  @NotBlank private String source;     // MANUAL/BULK

  @NotEmpty private List<Long> studentIds;

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
  public List<Long> getStudentIds() { return studentIds; }
  public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}
