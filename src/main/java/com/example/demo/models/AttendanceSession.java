// src/main/java/com/example/demo/models/AttendanceSession.java
package com.example.demo.models;

import com.example.demo.models.enums.AttendanceSessionStatus;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "attendance_sessions",
    uniqueConstraints = @UniqueConstraint(
      name = "uk_group_sched_date", columnNames = {"group_id","schedule_id","session_date"}))
public class AttendanceSession {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false) @JoinColumn(name = "group_id")
  private StudyGroup group;

  @ManyToOne(optional = false) @JoinColumn(name = "schedule_id")
  private GroupSchedule schedule;

  @Column(name = "session_date", nullable = false)
  private LocalDate sessionDate;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 12, nullable = false)
  private AttendanceSessionStatus status;

  @Column(name = "started_at")
  private OffsetDateTime startedAt;

  @Column(name = "closed_at")
  private OffsetDateTime closedAt;

  // getters/setters
  public Long getId() { return id; }

  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }

  public GroupSchedule getSchedule() { return schedule; }
  public void setSchedule(GroupSchedule schedule) { this.schedule = schedule; }

  public LocalDate getSessionDate() { return sessionDate; }
  public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

  public LocalTime getStartTime() { return startTime; }
  public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

  public LocalTime getEndTime() { return endTime; }
  public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

  public AttendanceSessionStatus getStatus() { return status; }
  public void setStatus(AttendanceSessionStatus status) { this.status = status; }

  public OffsetDateTime getStartedAt() { return startedAt; }
  public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

  public OffsetDateTime getClosedAt() { return closedAt; }
  public void setClosedAt(OffsetDateTime closedAt) { this.closedAt = closedAt; }
}
