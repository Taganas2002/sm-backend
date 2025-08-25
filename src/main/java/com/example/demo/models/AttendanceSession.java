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
         @Index(name = "idx_att_sess_group_date", columnList = "group_id, session_date")
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
  @Column(name = "status", nullable = false)
  private SessionStatus status = SessionStatus.PENDING_APPROVAL; // created on first scan

  @Enumerated(EnumType.STRING)
  @Column(name = "approved_by")
  private ApproverType approvedBy; // TEACHER / ADMIN

  @Column(name = "approved_at")
  private OffsetDateTime approvedAt;

  @Column(name = "opened_at")
  private OffsetDateTime openedAt;

  @Column(name = "closed_at")
  private OffsetDateTime closedAt;

  // Optional running counter (we still compute via query for accuracy)
  @Column(name = "student_scans", nullable = false)
  private int studentScans = 0;

  @Column(name="created_at", nullable=false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name="updated_at", nullable=false)
  private OffsetDateTime updatedAt = OffsetDateTime.now();

  @PreUpdate
  void touch(){ this.updatedAt = OffsetDateTime.now(); }

  // getters & setters
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

  public int getStudentScans() { return studentScans; }
  public void setStudentScans(int studentScans) { this.studentScans = studentScans; }

  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

  @Override public boolean equals(Object o){ return o instanceof AttendanceSession s && id!=null && id.equals(s.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
