package com.example.demo.dto.response;

import com.example.demo.models.enums.EnrollmentStatus;
import java.time.LocalDate;

public class EnrollmentResponse {
  private Long id;
  private Long studentId;
  private Long groupId;
  private LocalDate enrollmentDate;
  private EnrollmentStatus status;
  private String notes;

  public Long getId(){ return id; } public void setId(Long v){ this.id = v; }
  public Long getStudentId(){ return studentId; } public void setStudentId(Long v){ this.studentId = v; }
  public Long getGroupId(){ return groupId; } public void setGroupId(Long v){ this.groupId = v; }
  public LocalDate getEnrollmentDate(){ return enrollmentDate; } public void setEnrollmentDate(LocalDate v){ this.enrollmentDate = v; }
  public EnrollmentStatus getStatus(){ return status; } public void setStatus(EnrollmentStatus v){ this.status = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }
}
