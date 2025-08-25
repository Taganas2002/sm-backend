package com.example.demo.dto.response;

import com.example.demo.models.enums.SessionStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SessionSummaryDTO {
  public Long sessionId;
  public Long groupId;
  public LocalDate date;
  public LocalTime startTime;
  public LocalTime endTime;
  public SessionStatus status;
  public String approvedBy; // TEACHER / ADMIN / null
  public int studentScans;
  public List<SessionStudentDTO> students; // optional (only for /live)
}
