package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseUpsertRequest {
  private Long schoolId;
  private String category;
  private String subCategory;
  private String vendor;
  private String reference;
  private String method;
  private LocalDate expenseDate;     // yyyy-MM-dd
  private BigDecimal amount;
  private BigDecimal taxAmount;      // optional
  private String notes;

  // getters/setters
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getSubCategory() { return subCategory; }
  public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
  public String getVendor() { return vendor; }
  public void setVendor(String vendor) { this.vendor = vendor; }
  public String getReference() { return reference; }
  public void setReference(String reference) { this.reference = reference; }
  public String getMethod() { return method; }
  public void setMethod(String method) { this.method = method; }
  public LocalDate getExpenseDate() { return expenseDate; }
  public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  public BigDecimal getTaxAmount() { return taxAmount; }
  public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}
