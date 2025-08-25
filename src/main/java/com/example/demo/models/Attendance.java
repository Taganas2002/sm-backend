// com/example/demo/models/Attendance.java
package com.example.demo.models;

import com.example.demo.models.enums.CheckinSource;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "attendances",
       uniqueConstraints = @UniqueConstraint(name="uk_att_unique", columnNames = {"session_id","student_id"}),
       indexes = { @Index(name="idx_att_session", columnList = "session_id"),
                   @Index(name="idx_att_student", columnList = "student_id") })
public class Attendance {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="session_id", nullable = false)
  private AttendanceSession session;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="student_id", nullable = false)
  private Student student;

  @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10)
  private CheckinSource source = CheckinSource.QR;

  @Column(name="scanned_at", nullable = false)
  private OffsetDateTime scannedAt = OffsetDateTime.now();

  // getters/setters
  public Long getId() { return id; }
  public AttendanceSession getSession() { return session; }
  public void setSession(AttendanceSession session) { this.session = session; }
  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }
  public CheckinSource getSource() { return source; }
  public void setSource(CheckinSource source) { this.source = source; }
  public OffsetDateTime getScannedAt() { return scannedAt; }
  public void setScannedAt(OffsetDateTime scannedAt) { this.scannedAt = scannedAt; }
}
