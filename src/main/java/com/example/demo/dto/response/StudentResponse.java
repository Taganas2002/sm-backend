package com.example.demo.dto.response;

import java.time.LocalDate;

public class StudentResponse {
  private Long id;
  private Long schoolId;
  private Long levelId;
  private Long sectionId;

  private String fullName;
  private String photoUrl;
  private LocalDate dob;
  private String gender;
  private String address;
  private String phone;
  private String email;
  private String guardianName;
  private String guardianPhone;
  private LocalDate enrollmentDate;
  private String cardUid;
  private String medicalNotes;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
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
