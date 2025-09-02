package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProfitAndLossResponse {
  private LocalDate from;         // inclusive
  private LocalDate to;           // exclusive
  private BigDecimal income;      // student_payments.amount_paid
  private BigDecimal teacherCost; // teacher_payouts.total_amount
  private BigDecimal expenses;    // expenses.total_amount
  private BigDecimal net;         // income - teacherCost - expenses

  public LocalDate getFrom() { return from; }
  public void setFrom(LocalDate from) { this.from = from; }
  public LocalDate getTo() { return to; }
  public void setTo(LocalDate to) { this.to = to; }
  public BigDecimal getIncome() { return income; }
  public void setIncome(BigDecimal income) { this.income = income; }
  public BigDecimal getTeacherCost() { return teacherCost; }
  public void setTeacherCost(BigDecimal teacherCost) { this.teacherCost = teacherCost; }
  public BigDecimal getExpenses() { return expenses; }
  public void setExpenses(BigDecimal expenses) { this.expenses = expenses; }
  public BigDecimal getNet() { return net; }
  public void setNet(BigDecimal net) { this.net = net; }
}
