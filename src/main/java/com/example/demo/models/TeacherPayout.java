// src/main/java/com/example/demo/models/TeacherPayout.java
package com.example.demo.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "teacher_payouts")
public class TeacherPayout {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "payout_no", length = 40, nullable = false, unique = true)
  private String payoutNo;

  @ManyToOne(optional = false) @JoinColumn(name = "teacher_id")
  private Teacher teacher;

  @Column(name = "issued_at", nullable = false)
  private OffsetDateTime issuedAt;

  @Column(name = "method", length = 30)
  private String method;

  @Column(name = "reference", length = 120)
  private String reference;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "cashier_user_id")
  private Long cashierUserId;

  // getters/setters
  public Long getId() { return id; }
  public String getPayoutNo() { return payoutNo; }
  public void setPayoutNo(String payoutNo) { this.payoutNo = payoutNo; }
  public Teacher getTeacher() { return teacher; }
  public void setTeacher(Teacher teacher) { this.teacher = teacher; }
  public OffsetDateTime getIssuedAt() { return issuedAt; }
  public void setIssuedAt(OffsetDateTime issuedAt) { this.issuedAt = issuedAt; }
  public String getMethod() { return method; }
  public void setMethod(String method) { this.method = method; }
  public String getReference() { return reference; }
  public void setReference(String reference) { this.reference = reference; }
  public BigDecimal getTotalAmount() { return totalAmount; }
  public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
  public Long getCashierUserId() { return cashierUserId; }
  public void setCashierUserId(Long cashierUserId) { this.cashierUserId = cashierUserId; }
}
