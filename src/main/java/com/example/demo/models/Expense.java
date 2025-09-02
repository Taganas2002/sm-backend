// src/main/java/com/example/demo/models/Expense.java
package com.example.demo.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "expenses")
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // optional: tie to a school if you have multi-school
  @Column(name = "school_id")
  private Long schoolId;

  @Column(name = "category", length = 60, nullable = false)
  private String category; // "ELECTRICITY", "WATER", "RENT", "OTHER", ...

  @Column(name = "sub_category", length = 60)
  private String subCategory; // optional finer category

  @Column(name = "vendor", length = 120)
  private String vendor;

  @Column(name = "reference", length = 120)
  private String reference; // invoice#, bill#, etc.

  @Column(name = "method", length = 30)
  private String method; // CASH | BANK | CARD | ...

  @Column(name = "expense_date", nullable = false)
  private LocalDate expenseDate; // when the cost occurred

  @Column(name = "amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal amount; // base amount (before tax), or total if you prefer

  @Column(name = "tax_amount", precision = 18, scale = 2)
  private BigDecimal taxAmount; // optional; default 0

  @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal totalAmount; // amount + taxAmount (computed)

  @Lob
  private String notes;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt = OffsetDateTime.now();

  @PreUpdate
  void touch() {
    updatedAt = OffsetDateTime.now();
  }

  @PrePersist
  void onCreate() {
    if (createdAt == null) createdAt = OffsetDateTime.now();
    if (updatedAt == null) updatedAt = createdAt;
    if (taxAmount == null) taxAmount = BigDecimal.ZERO;
    if (totalAmount == null) totalAmount =
        (amount == null ? BigDecimal.ZERO : amount).add(taxAmount);
  }

  // =========================
  // Getters and Setters
  // =========================

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSchoolId() {
    return schoolId;
  }

  public void setSchoolId(Long schoolId) {
    this.schoolId = schoolId;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getSubCategory() {
    return subCategory;
  }

  public void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  }

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public LocalDate getExpenseDate() {
    return expenseDate;
  }

  public void setExpenseDate(LocalDate expenseDate) {
    this.expenseDate = expenseDate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getTaxAmount() {
    return taxAmount;
  }

  public void setTaxAmount(BigDecimal taxAmount) {
    this.taxAmount = taxAmount;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
