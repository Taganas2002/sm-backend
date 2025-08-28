package com.example.demo.dto.response;

public class StudentPresentResponse {
  public String status;       // CHECKED_IN / ALREADY_CHECKED_IN / WAITING_FOR_TEACHER
  public Long sessionId;      // if opened
  public Integer presentCount;
  public String slotDate;     // when waiting
  public String startTime;    // when waiting
  public String endTime;      // when waiting
  public String message;
}
