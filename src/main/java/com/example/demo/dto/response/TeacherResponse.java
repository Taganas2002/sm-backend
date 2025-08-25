package com.example.demo.dto.response;

import java.time.OffsetDateTime;

public class TeacherResponse {
  private Long id;
  private Long schoolId;
  private String fullName;
  private String phone;
  private String email;
  private String cardUid;
  private String qrCode;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
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
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
