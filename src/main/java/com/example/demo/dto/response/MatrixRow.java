package com.example.demo.dto.response;

import java.util.List;

/** One row per student with cells across the date range. */
public class MatrixRow {
  private Long studentId;
  private String studentName;

  private long presentCount;
  private long absentCount;

  private List<MatrixCell> cells;

  public Long getStudentId() { return studentId; }
  public void setStudentId(Long studentId) { this.studentId = studentId; }
  public String getStudentName() { return studentName; }
  public void setStudentName(String studentName) { this.studentName = studentName; }
  public long getPresentCount() { return presentCount; }
  public void setPresentCount(long presentCount) { this.presentCount = presentCount; }
  public long getAbsentCount() { return absentCount; }
  public void setAbsentCount(long absentCount) { this.absentCount = absentCount; }
  public List<MatrixCell> getCells() { return cells; }
  public void setCells(List<MatrixCell> cells) { this.cells = cells; }
}
