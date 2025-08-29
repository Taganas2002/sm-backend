package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {
  private Long sessionId;
  private Long groupId;
  private Long scheduleId;
  private LocalDate date;
  /** OPEN | CLOSED | PENDING */
  private String status;
  private Long presentCount;
  private Long absentCount;
  /** only when PENDING (pre-scans exist) */
  private Long preScanCount;
  /** NEW: for UI checkboxes */
  private List<Long> presentStudentIds;

  public SessionResponse() {}

  public SessionResponse(Long sessionId, Long groupId, Long scheduleId, LocalDate date,
                         String status, long presentCount, long absentCount) {
    this.sessionId = sessionId;
    this.groupId = groupId;
    this.scheduleId = scheduleId;
    this.date = date;
    this.status = status;
    this.presentCount = presentCount;
    this.absentCount = absentCount;
  }

  public static SessionResponse pending(Long groupId, Long scheduleId, LocalDate date, Long preScanCount) {
    SessionResponse r = new SessionResponse(null, groupId, scheduleId, date, "PENDING", 0, 0);
    r.setPreScanCount(preScanCount);
    return r;
  }

  // getters / setters
  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }
  public Long getScheduleId() { return scheduleId; }
  public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
  public LocalDate getDate() { return date; }
  public void setDate(LocalDate date) { this.date = date; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Long getPresentCount() { return presentCount; }
  public void setPresentCount(Long presentCount) { this.presentCount = presentCount; }
  public Long getAbsentCount() { return absentCount; }
  public void setAbsentCount(Long absentCount) { this.absentCount = absentCount; }
  public Long getPreScanCount() { return preScanCount; }
  public void setPreScanCount(Long preScanCount) { this.preScanCount = preScanCount; }
  public List<Long> getPresentStudentIds() { return presentStudentIds; }
  public void setPresentStudentIds(List<Long> presentStudentIds) { this.presentStudentIds = presentStudentIds; }
}
