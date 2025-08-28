package com.example.demo.models;

import com.example.demo.models.enums.AttendanceMark;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "student_attendance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "student_id"}))
public class StudentAttendance {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  private AttendanceSession session;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

//PRESENT / ABSENT
 @Enumerated(EnumType.STRING)
 @Column(name = "status", nullable = false, length = 12)
 private AttendanceMark status = AttendanceMark.PRESENT;
  


  @Column(name = "checked_in_at")
  private OffsetDateTime checkedInAt; // only when PRESENT

  @Column(name = "source", length = 16)
  private String source; // QR / MANUAL / BULK / AUTO

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt = OffsetDateTime.now();

  @PreUpdate
  void touch(){ this.updatedAt = OffsetDateTime.now(); }

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public AttendanceSession getSession() { return session; }
  public void setSession(AttendanceSession session) { this.session = session; }
  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }
  public AttendanceMark getStatus() { return status; }
  public void setStatus(AttendanceMark status) { this.status = status; }
  public OffsetDateTime getCheckedInAt() { return checkedInAt; }
  public void setCheckedInAt(OffsetDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

  @Override public boolean equals(Object o){ return o instanceof StudentAttendance a && id!=null && id.equals(a.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
