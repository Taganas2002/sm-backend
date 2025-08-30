package com.example.demo.models;

import com.example.demo.models.StudyGroup;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "receipt_lines")
public class ReceiptLine {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="receipt_id", nullable=false)
  private Receipt receipt;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="group_id")
  private StudyGroup group;

  @Column(name="model", length=20, nullable=false)
  private String model; // MONTHLY | PER_SESSION | PER_HOUR

  @Column(name="period", length=7)  // YYYY-MM
  private String period;

  @Column(name="sessions")
  private Integer sessions;

  @Column(name="hours", precision = 10, scale = 2)
  private BigDecimal hours;

  @Column(name="amount", precision = 14, scale = 2, nullable=false)
  private BigDecimal amount;

  // getters/setters
  public Long getId() { return id; }
  public Receipt getReceipt() { return receipt; }
  public void setReceipt(Receipt receipt) { this.receipt = receipt; }
  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }
  public String getModel() { return model; }
  public void setModel(String model) { this.model = model; }
  public String getPeriod() { return period; }
  public void setPeriod(String period) { this.period = period; }
  public Integer getSessions() { return sessions; }
  public void setSessions(Integer sessions) { this.sessions = sessions; }
  public BigDecimal getHours() { return hours; }
  public void setHours(BigDecimal hours) { this.hours = hours; }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
}
