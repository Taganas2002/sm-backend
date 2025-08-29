// src/main/java/com/example/demo/models/StudentPreCheckin.java
package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "student_precheckin",
       uniqueConstraints = @UniqueConstraint(
         name = "uk_prescan", columnNames = {"group_id","schedule_id","session_date","student_id"}))
public class StudentPreCheckin {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false) @JoinColumn(name = "group_id")
  private StudyGroup group;

  @ManyToOne(optional = false) @JoinColumn(name = "schedule_id")
  private GroupSchedule schedule;

  @ManyToOne(optional = false) @JoinColumn(name = "student_id")
  private Student student;

  @Column(name = "session_date", nullable = false)
  private LocalDate sessionDate;

  @Column(name = "scanned_at", nullable = false)
  private OffsetDateTime scannedAt;

  // getters/setters
  public Long getId() { return id; }
  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }
  public GroupSchedule getSchedule() { return schedule; }
  public void setSchedule(GroupSchedule schedule) { this.schedule = schedule; }
  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }
  public LocalDate getSessionDate() { return sessionDate; }
  public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
  public OffsetDateTime getScannedAt() { return scannedAt; }
  public void setScannedAt(OffsetDateTime scannedAt) { this.scannedAt = scannedAt; }
}
