package com.example.demo.dto.response;

import java.util.List;

public class SessionDetailResponse {
  public Long sessionId;
  public Long groupId;
  public String date;        // yyyy-MM-dd
  public String startTime;   // HH:mm
  public String endTime;     // HH:mm
  public String status;      // PLANNED/OPEN/CLOSED
  public List<StudentRow> students;

  public static class StudentRow {
    public Long studentId;
    public String name;
    public String checkedInAt;       // ISO or null
    public String attendanceStatus;  // PRESENT/ABSENT
  }
}
