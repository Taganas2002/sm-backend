package com.example.demo.models;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "teachers")
public class Teacher {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // optional: a teacher can belong to a school
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id")
  private School school;

  @Column(name = "full_name", length = 120)
  private String fullName;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "email", length = 120)
  private String email;

  // for QR/RFID card scans in attendance
  @Column(name = "card_uid", length = 64, unique = true)
  private String cardUid;

  // optional: if you also issue a plain QR string
  @Column(name = "qr_code", length = 128, unique = true)
  private String qrCode;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt = OffsetDateTime.now();

  @PreUpdate
  void touch() { this.updatedAt = OffsetDateTime.now(); }

  // ===== getters / setters =====
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public School getSchool() { return school; }
  public void setSchool(School school) { this.school = school; }

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

  @Override public boolean equals(Object o) { return o instanceof Teacher t && id != null && id.equals(t.id); }
  @Override public int hashCode() { return Objects.hashCode(id); }
}
