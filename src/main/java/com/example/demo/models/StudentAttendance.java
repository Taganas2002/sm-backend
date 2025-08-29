// src/main/java/com/example/demo/models/StudentAttendance.java
package com.example.demo.models;

import com.example.demo.models.enums.StudentAttendanceStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "student_attendance",
  uniqueConstraints = @UniqueConstraint(
    name = "uk_session_student", columnNames = {"session_id","student_id"}))
public class StudentAttendance {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false) @JoinColumn(name = "session_id")
  private AttendanceSession session;

  @ManyToOne(optional = false) @JoinColumn(name = "student_id")
  private Student student;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 12, nullable = false)
  private StudentAttendanceStatus status;

  @Column(name = "checked_in_at")
  private OffsetDateTime checkedInAt;

  // getters/setters
  public Long getId() { return id; }
  public AttendanceSession getSession() { return session; }
  public void setSession(AttendanceSession session) { this.session = session; }
  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }
  public StudentAttendanceStatus getStatus() { return status; }
  public void setStatus(StudentAttendanceStatus status) { this.status = status; }
  public OffsetDateTime getCheckedInAt() { return checkedInAt; }
  public void setCheckedInAt(OffsetDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
}
