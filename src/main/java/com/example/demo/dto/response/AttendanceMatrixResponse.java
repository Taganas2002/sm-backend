package com.example.demo.dto.response;

import java.time.LocalDate;
import java.util.List;

public class AttendanceMatrixResponse {

  // optional meta (safe if you want to set them later)
  private Long groupId;
  private LocalDate start;
  private LocalDate endExclusive;

  // required for your UI
  private List<String> dates;                 // ["2025-08-29", ...]
  private List<StudentLite> students;         // rows

  // ---- getters/setters ----
  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }

  public LocalDate getStart() { return start; }
  public void setStart(LocalDate start) { this.start = start; }

  public LocalDate getEndExclusive() { return endExclusive; }
  public void setEndExclusive(LocalDate endExclusive) { this.endExclusive = endExclusive; }

  public List<String> getDates() { return dates; }
  public void setDates(List<String> dates) { this.dates = dates; }

  public List<StudentLite> getStudents() { return students; }
  public void setStudents(List<StudentLite> students) { this.students = students; }

  // ---------- row DTO ----------
  public static class StudentLite {
    private Long studentId;
    private String studentName;   // comes from Student.fullName
    private List<String> cells;   // e.g. ["P", "A", "", ...]

    public StudentLite() {}

    public StudentLite(Long studentId, String studentName, List<String> cells) {
      this.studentId = studentId;
      this.studentName = studentName;
      this.cells = cells;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public List<String> getCells() { return cells; }
    public void setCells(List<String> cells) { this.cells = cells; }
  }
}
