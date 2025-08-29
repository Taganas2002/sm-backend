package com.example.demo.dto.request;

import com.example.demo.models.StudentAttendance;

import java.util.List;

public class BulkPresentRequest {
  private List<Long> studentIds;
  private StudentAttendance.Source source = StudentAttendance.Source.MANUAL;

  public List<Long> getStudentIds() { return studentIds; }
  public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
  public StudentAttendance.Source getSource() { return source; }
  public void setSource(StudentAttendance.Source source) { this.source = source; }
}
