package com.example.demo.dto.response;

import java.time.LocalDate;

public class TeacherResponse {
  private Long id;
  private Long schoolId;
  private String fullName;
  private String gender;
  private String address;
  private String phone;
  private String email;
  private LocalDate employmentDate;
  private String notes;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
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
