package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
  name = "teachers",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"phone"}),
    @UniqueConstraint(columnNames = {"email"})
  }
)
public class Teacher {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id")
  private School school; // optional

  @Column(name = "full_name", nullable = false, length = 160)
  private String fullName;

  @Column(length = 20)
  private String gender;

  @Column(length = 255)
  private String address;

  @Column(length = 60)
  private String phone;

  @Column(length = 160)
  private String email;

  @Column(name = "employment_date")
  private LocalDate employmentDate;

  @Column(columnDefinition = "TEXT")
  private String notes;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public School getSchool() { return school; }
  public void setSchool(School school) { this.school = school; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getGender() { return gender; }
  public void setGender(String gender) { this.gender = gender; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public LocalDate getEmploymentDate() { return employmentDate; }
  public void setEmploymentDate(LocalDate employmentDate) { this.employmentDate = employmentDate; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}
