package com.example.demo.models;

import com.example.demo.models.enums.EnrollmentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
  name = "enrollments",
  uniqueConstraints = @UniqueConstraint(columnNames = {"student_id","group_id"})
)
public class Enrollment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "group_id", nullable = false)
  private StudyGroup group;

  @Column(name = "enrollment_date", nullable = false)
  private LocalDate enrollmentDate;

  @Enumerated(EnumType.STRING) @Column(nullable = false)
  private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

  @Lob private String notes;

  public Long getId(){ return id; } public void setId(Long id){ this.id = id; }
  public Student getStudent(){ return student; } public void setStudent(Student student){ this.student = student; }
  public StudyGroup getGroup(){ return group; } public void setGroup(StudyGroup group){ this.group = group; }
  public LocalDate getEnrollmentDate(){ return enrollmentDate; } public void setEnrollmentDate(LocalDate v){ this.enrollmentDate = v; }
  public EnrollmentStatus getStatus(){ return status; } public void setStatus(EnrollmentStatus v){ this.status = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }

  @Override public boolean equals(Object o){ return o instanceof Enrollment e && id!=null && id.equals(e.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
