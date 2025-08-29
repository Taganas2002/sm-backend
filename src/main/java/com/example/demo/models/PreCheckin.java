// src/main/java/com/example/demo/models/PreCheckin.java
package com.example.demo.models;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "pre_checkins",
  uniqueConstraints = @UniqueConstraint(
    name = "uk_precheckin_key",
    columnNames = {"group_id","schedule_id","session_date","student_id"}))
public class PreCheckin {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false) @JoinColumn(name = "group_id")
  private StudyGroup group;

  @ManyToOne(optional = false) @JoinColumn(name = "schedule_id")
  private GroupSchedule schedule;

  @Column(name = "session_date", nullable = false)
  private LocalDate sessionDate;

  @ManyToOne(optional = false) @JoinColumn(name = "student_id")
  private Student student;

  @Column(name = "scanned_at", nullable = false)
  private OffsetDateTime scannedAt;

  // getters/setters
  public Long getId() { return id; }
  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }
  public GroupSchedule getSchedule() { return schedule; }
  public void setSchedule(GroupSchedule schedule) { this.schedule = schedule; }
  public LocalDate getSessionDate() { return sessionDate; }
  public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }
  public OffsetDateTime getScannedAt() { return scannedAt; }
  public void setScannedAt(OffsetDateTime scannedAt) { this.scannedAt = scannedAt; }
}
