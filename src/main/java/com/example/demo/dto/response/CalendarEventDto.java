package com.example.demo.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;

public class CalendarEventDto {
  private String id;                  // e.g. "sched-12-2025-08-25"
  private String title;               // e.g. "Maths (Mr Smith)"
  private OffsetDateTime start;       // ISO8601 for FullCalendar
  private OffsetDateTime end;
  private Map<String, Object> extendedProps;

  public CalendarEventDto() {}
  public CalendarEventDto(String id, String title, OffsetDateTime start, OffsetDateTime end,
                          Map<String, Object> extendedProps) {
    this.id = id;
    this.title = title;
    this.start = start;
    this.end = end;
    this.extendedProps = extendedProps;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public OffsetDateTime getStart() { return start; }
  public void setStart(OffsetDateTime start) { this.start = start; }
  public OffsetDateTime getEnd() { return end; }
  public void setEnd(OffsetDateTime end) { this.end = end; }
  public Map<String, Object> getExtendedProps() { return extendedProps; }
  public void setExtendedProps(Map<String, Object> extendedProps) { this.extendedProps = extendedProps; }
}
