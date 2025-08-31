// src/main/java/com/example/demo/models/TeacherEarning.java
package com.example.demo.models;

import com.example.demo.models.enums.EarningStatus;
import com.example.demo.models.enums.TeacherShareType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "teacher_earnings")
public class TeacherEarning {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false) @JoinColumn(name = "teacher_id")
  private Teacher teacher;

  @ManyToOne(optional = false) @JoinColumn(name = "group_id")
  private StudyGroup group;

  @ManyToOne(optional = false) @JoinColumn(name = "student_payment_id")
  private StudentPayment studentPayment;

  @Column(name = "recognized_at", nullable = false)
  private OffsetDateTime recognizedAt;

  @Column(name = "gross_amount", nullable = false)
  private BigDecimal grossAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "share_type", nullable = false, length = 30)
  private TeacherShareType shareType;

  @Column(name = "share_value") // percent(0..100) or fixed amount per unit
  private BigDecimal shareValue;

  @Column(name = "share_amount", nullable = false)
  private BigDecimal shareAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 12)
  private EarningStatus status = EarningStatus.UNPAID;

  @ManyToOne @JoinColumn(name = "payout_id")
  private TeacherPayout payout;

  // getters/setters
  public Long getId() { return id; }
  public Teacher getTeacher() { return teacher; }
  public void setTeacher(Teacher teacher) { this.teacher = teacher; }
  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }
  public StudentPayment getStudentPayment() { return studentPayment; }
  public void setStudentPayment(StudentPayment studentPayment) { this.studentPayment = studentPayment; }
  public OffsetDateTime getRecognizedAt() { return recognizedAt; }
  public void setRecognizedAt(OffsetDateTime recognizedAt) { this.recognizedAt = recognizedAt; }
  public BigDecimal getGrossAmount() { return grossAmount; }
  public void setGrossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; }
  public TeacherShareType getShareType() { return shareType; }
  public void setShareType(TeacherShareType shareType) { this.shareType = shareType; }
  public BigDecimal getShareValue() { return shareValue; }
  public void setShareValue(BigDecimal shareValue) { this.shareValue = shareValue; }
  public BigDecimal getShareAmount() { return shareAmount; }
  public void setShareAmount(BigDecimal shareAmount) { this.shareAmount = shareAmount; }
  public EarningStatus getStatus() { return status; }
  public void setStatus(EarningStatus status) { this.status = status; }
  public TeacherPayout getPayout() { return payout; }
  public void setPayout(TeacherPayout payout) { this.payout = payout; }
}
