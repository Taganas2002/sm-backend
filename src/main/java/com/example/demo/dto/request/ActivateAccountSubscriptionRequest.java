package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ActivateAccountSubscriptionRequest {

  @NotNull
  private Long userId;

  @Min(0) private Integer years  = 0;
  @Min(0) private Integer months = 0;
  @Min(0) private Integer days   = 0;

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public Integer getYears() { return years; }
  public void setYears(Integer years) { this.years = years; }
  public Integer getMonths() { return months; }
  public void setMonths(Integer months) { this.months = months; }
  public Integer getDays() { return days; }
  public void setDays(Integer days) { this.days = days; }
}
