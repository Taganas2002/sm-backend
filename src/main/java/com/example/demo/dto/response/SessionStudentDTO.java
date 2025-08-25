package com.example.demo.dto.response;

import java.time.OffsetDateTime;

public class SessionStudentDTO {
  public Long studentId;
  public String name;
  public OffsetDateTime checkedInAt;

  public SessionStudentDTO(Long studentId, String name, OffsetDateTime checkedInAt) {
    this.studentId = studentId;
    this.name = name;
    this.checkedInAt = checkedInAt;
  }
}
