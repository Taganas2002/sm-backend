package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExpenseSummaryResponse {
  private LocalDate from;
  private LocalDate to; // exclusive
  private BigDecimal total;
  private List<ExpenseCategoryTotal> byCategory;

  public LocalDate getFrom() { return from; }
  public void setFrom(LocalDate from) { this.from = from; }
  public LocalDate getTo() { return to; }
  public void setTo(LocalDate to) { this.to = to; }
  public BigDecimal getTotal() { return total; }
  public void setTotal(BigDecimal total) { this.total = total; }
  public List<ExpenseCategoryTotal> getByCategory() { return byCategory; }
  public void setByCategory(List<ExpenseCategoryTotal> byCategory) { this.byCategory = byCategory; }
}
