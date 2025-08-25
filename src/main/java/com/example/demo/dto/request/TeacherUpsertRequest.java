package com.example.demo.dto.request;

public class TeacherUpsertRequest {
  private String fullName;
  private String phone;
  private String email;
  private String cardUid; // for scans
  private String qrCode;  // optional
  private Long schoolId;  // nullable

  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCardUid() { return cardUid; }
  public void setCardUid(String cardUid) { this.cardUid = cardUid; }
  public String getQrCode() { return qrCode; }
  public void setQrCode(String qrCode) { this.qrCode = qrCode; }
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
}
