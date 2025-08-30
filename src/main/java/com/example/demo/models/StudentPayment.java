package com.example.demo.models;

import com.example.demo.models.Student;
import com.example.demo.models.StudyGroup;
import com.example.demo.models.enums.PaymentType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "student_payments")
public class StudentPayment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private StudyGroup group;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_type", length = 20, nullable = false)
  private PaymentType paymentType; // MONTHLY | PER_SESSION | PER_HOUR

  @Column(name = "month_year", length = 7) // YYYY-MM (for MONTHLY)
  private String monthYear;

  @Column(name = "sessions_paid")
  private Integer sessionsPaid;

  @Column(name = "hours_paid", precision = 10, scale = 2)
  private BigDecimal hoursPaid;

  @Column(name = "amount_paid", precision = 14, scale = 2, nullable = false)
  private BigDecimal amountPaid = BigDecimal.ZERO;

  @Column(name = "amount_due", precision = 14, scale = 2, nullable = false)
  private BigDecimal amountDue = BigDecimal.ZERO;

  // 🔹 NEW: map BALANCE (your DB requires it)
  @Column(name = "balance", precision = 14, scale = 2, nullable = false)
  private BigDecimal balance = BigDecimal.ZERO;

  @Column(name = "payment_date", nullable = false)
  private OffsetDateTime paymentDate;

  // getters/setters
  public Long getId() { return id; }
  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }
  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }
  public PaymentType getPaymentType() { return paymentType; }
  public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
  public String getMonthYear() { return monthYear; }
  public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
  public Integer getSessionsPaid() { return sessionsPaid; }
  public void setSessionsPaid(Integer sessionsPaid) { this.sessionsPaid = sessionsPaid; }
  public BigDecimal getHoursPaid() { return hoursPaid; }
  public void setHoursPaid(BigDecimal hoursPaid) { this.hoursPaid = hoursPaid; }
  public BigDecimal getAmountPaid() { return amountPaid; }
  public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
  public BigDecimal getAmountDue() { return amountDue; }
  public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }
  public BigDecimal getBalance() { return balance; }
  public void setBalance(BigDecimal balance) { this.balance = balance; }
  public OffsetDateTime getPaymentDate() { return paymentDate; }
  public void setPaymentDate(OffsetDateTime paymentDate) { this.paymentDate = paymentDate; }
}
