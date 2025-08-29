package com.example.demo.dto.response;

/** Column header meta for a given date (and optionally the slot). */
public class MatrixColumn {
  private String date;        // YYYY-MM-DD
  private String dayOfWeek;   // MONDAY..SUNDAY
  private String startTime;   // HH:mm (nullable if not scheduled)
  private String endTime;     // HH:mm (nullable if not scheduled)
  private Long classroomId;   // nullable
  private String classroomName; // nullable

  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  public String getDayOfWeek() { return dayOfWeek; }
  public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
  public String getStartTime() { return startTime; }
  public void setStartTime(String startTime) { this.startTime = startTime; }
  public String getEndTime() { return endTime; }
  public void setEndTime(String endTime) { this.endTime = endTime; }
  public Long getClassroomId() { return classroomId; }
  public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
  public String getClassroomName() { return classroomName; }
  public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
}
