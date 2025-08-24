package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class StudentUpsertRequest {
  private Long schoolId;   // optional
  private Long levelId;    // optional
  private Long sectionId;  // optional

  @NotBlank @Size(max = 160)
  private String fullName;

  @Size(max = 255) private String photoUrl;
  private LocalDate dob;

  @Size(max = 20)  private String gender;
  @Size(max = 255) private String address;
  @Size(max = 60)  private String phone;
  @Size(max = 160) private String email;

  @Size(max = 160) private String guardianName;
  @Size(max = 60)  private String guardianPhone;

  private LocalDate enrollmentDate;

  @NotBlank @Size(max = 100)
  private String cardUid; // unique

  @Size(max = 65535) private String medicalNotes;

  // getters/setters
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
  public Long getLevelId() { return levelId; }
  public void setLevelId(Long levelId) { this.levelId = levelId; }
  public Long getSectionId() { return sectionId; }
  public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getPhotoUrl() { return photoUrl; }
  public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
  public LocalDate getDob() { return dob; }
  public void setDob(LocalDate dob) { this.dob = dob; }
  public String getGender() { return gender; }
  public void setGender(String gender) { this.gender = gender; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getGuardianName() { return guardianName; }
  public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
  public String getGuardianPhone() { return guardianPhone; }
  public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }
  public LocalDate getEnrollmentDate() { return enrollmentDate; }
  public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
  public String getCardUid() { return cardUid; }
  public void setCardUid(String cardUid) { this.cardUid = cardUid; }
  public String getMedicalNotes() { return medicalNotes; }
  public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }
}
