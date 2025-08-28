package com.example.demo.models;

import com.example.demo.models.enums.ApproverType;
import com.example.demo.models.enums.SessionStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "attendance_sessions",
       indexes = {
         @Index(name = "idx_session_group_date", columnList = "group_id, session_date"),
         @Index(name = "idx_session_slot", columnList = "group_id, session_date, start_time, end_time", unique = true)
       })
public class AttendanceSession {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "group_id", nullable = false)
  private StudyGroup group;

  @Column(name = "session_date", nullable = false)
  private LocalDate sessionDate;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 24)
  private SessionStatus status = SessionStatus.PLANNED;

  @Enumerated(EnumType.STRING)
  @Column(name = "approved_by", length = 16)
  private ApproverType approvedBy;

  @Column(name = "approved_at")
  private OffsetDateTime approvedAt;

  @Column(name = "opened_at")
  private OffsetDateTime openedAt;

  @Column(name = "closed_at")
  private OffsetDateTime closedAt;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }
  public LocalDate getSessionDate() { return sessionDate; }
  public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
  public LocalTime getStartTime() { return startTime; }
  public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
  public LocalTime getEndTime() { return endTime; }
  public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
  public SessionStatus getStatus() { return status; }
  public void setStatus(SessionStatus status) { this.status = status; }
  public ApproverType getApprovedBy() { return approvedBy; }
  public void setApprovedBy(ApproverType approvedBy) { this.approvedBy = approvedBy; }
  public OffsetDateTime getApprovedAt() { return approvedAt; }
  public void setApprovedAt(OffsetDateTime approvedAt) { this.approvedAt = approvedAt; }
  public OffsetDateTime getOpenedAt() { return openedAt; }
  public void setOpenedAt(OffsetDateTime openedAt) { this.openedAt = openedAt; }
  public OffsetDateTime getClosedAt() { return closedAt; }
  public void setClosedAt(OffsetDateTime closedAt) { this.closedAt = closedAt; }

  @Override public boolean equals(Object o){ return o instanceof AttendanceSession s && id!=null && id.equals(s.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
