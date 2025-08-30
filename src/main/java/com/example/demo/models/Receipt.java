package com.example.demo.models;

import com.example.demo.models.Student;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipts")
public class Receipt {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="receipt_no", nullable=false, unique=true, length = 40)
  private String receiptNo;

  @Column(name="issued_at", nullable=false)
  private OffsetDateTime issuedAt;

  @Column(name="method", length=30)
  private String method;

  @Column(name="reference", length=100)
  private String reference;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="student_id", nullable=false)
  private Student student;

  @Column(name="cashier_user_id")
  private Long cashierUserId;

  @Column(name="total_amount", nullable=false)
  private BigDecimal totalAmount = BigDecimal.ZERO;

  @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReceiptLine> lines = new ArrayList<>();

  public void addLine(ReceiptLine l){ lines.add(l); l.setReceipt(this); }

  // getters/setters
  public Long getId() { return id; }
  public String getReceiptNo() { return receiptNo; }
  public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
  public OffsetDateTime getIssuedAt() { return issuedAt; }
  public void setIssuedAt(OffsetDateTime issuedAt) { this.issuedAt = issuedAt; }
  public String getMethod() { return method; }
  public void setMethod(String method) { this.method = method; }
  public String getReference() { return reference; }
  public void setReference(String reference) { this.reference = reference; }
  public Student getStudent() { return student; }
  public void setStudent(Student student) { this.student = student; }
  public Long getCashierUserId() { return cashierUserId; }
  public void setCashierUserId(Long cashierUserId) { this.cashierUserId = cashierUserId; }
  public BigDecimal getTotalAmount() { return totalAmount; }
  public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
  public List<ReceiptLine> getLines() { return lines; }
}
