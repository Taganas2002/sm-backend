package com.example.demo.dto.request;

import com.example.demo.models.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class EnrollmentCreateRequest {
  @NotNull private Long studentId;
  @NotNull private Long groupId;
  @NotNull private LocalDate enrollmentDate;
  private EnrollmentStatus status; // optional, defaults to ACTIVE
  private String notes;

  public Long getStudentId(){ return studentId; } public void setStudentId(Long v){ this.studentId = v; }
  public Long getGroupId(){ return groupId; } public void setGroupId(Long v){ this.groupId = v; }
  public LocalDate getEnrollmentDate(){ return enrollmentDate; } public void setEnrollmentDate(LocalDate v){ this.enrollmentDate = v; }
  public EnrollmentStatus getStatus(){ return status; } public void setStatus(EnrollmentStatus v){ this.status = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }
}
