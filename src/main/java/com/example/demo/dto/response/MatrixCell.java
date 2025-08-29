package com.example.demo.dto.response;

public class MatrixCell {

  public enum Status {
    /** Student has a present mark for that session. */
    PRESENT,
    /** A session exists but student has no present mark (treated as absent). */
    ABSENT,
    /** There was no session that day (not planned or cancelled). */
    NOT_SCHEDULED,
    /** A session is planned but hasn't been started yet. */
    NOT_STARTED
  }

  private String date;          // YYYY-MM-DD (column date)
  private Long scheduleId;      // nullable
  private Long sessionId;       // nullable
  private Status status;
  private String markedAt;      // ISO-8601 if PRESENT; otherwise null

  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  public Long getScheduleId() { return scheduleId; }
  public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
  public String getMarkedAt() { return markedAt; }
  public void setMarkedAt(String markedAt) { this.markedAt = markedAt; }
}
