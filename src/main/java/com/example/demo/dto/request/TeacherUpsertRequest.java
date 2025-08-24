package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TeacherUpsertRequest {
  private Long schoolId;                       // optional

  @NotBlank @Size(max = 160)
  private String fullName;

  @Size(max = 20)  private String gender;
  @Size(max = 255) private String address;
  @Size(max = 60)  private String phone;
  @Size(max = 160) private String email;

  private LocalDate employmentDate;
  @Size(max = 65535) private String notes;

  // getters/setters
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
