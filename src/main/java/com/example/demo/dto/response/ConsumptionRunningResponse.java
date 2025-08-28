package com.example.demo.dto.response;

public class ConsumptionRunningResponse {
  public Long studentId;
  public Long groupId;
  public Integer attended;               // total events counted (PRESENT+ABSENT if your policy)
  public Integer quota;                  // group.sessionsPerMonth as bundle size
  public String  ratio;                  // e.g. "3/8"
  public Integer cyclesCompleted;
  public Integer currentCycleAttended;
  public Integer remainingInCurrentCycle;
  public boolean needsPayment;
}
