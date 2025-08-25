package com.example.demo.models;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "student_attendance",
       uniqueConstraints = @UniqueConstraint(name="uk_session_student", columnNames = {"session_id","student_id"}),
       indexes = {
         @Index(name="idx_attendance_session", columnList="session_id")
       })
public class StudentAttendance {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="session_id", nullable=false)
  private AttendanceSession session;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="student_id", nullable=false)
  private Student student;

  @Column(name="checked_in_at", nullable=false)
  private OffsetDateTime checkedInAt = OffsetDateTime.now();

  // getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public AttendanceSession getSession() { return session; }
  public void setSession(AttendanceSession session) { this.session = session; }

  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }

  public OffsetDateTime getCheckedInAt() { return checkedInAt; }
  public void setCheckedInAt(OffsetDateTime checkedInAt) { this.checkedInAt = checkedInAt; }

  @Override public boolean equals(Object o){ return o instanceof StudentAttendance s && id!=null && id.equals(s.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
