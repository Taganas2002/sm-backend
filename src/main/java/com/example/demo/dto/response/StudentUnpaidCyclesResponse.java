// src/main/java/com/example/demo/dto/response/StudentUnpaidCyclesResponse.java
package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentUnpaidCyclesResponse {
  private Long studentId;
  private String studentFullName;
  private int totalUnpaidCycles;
  private List<Group> groups = new ArrayList<>();

  public static class Group {
    public Long groupId;
    public String groupName;
    public Integer sessionsPerCycle;

    // NEW: always included so UI can show progress even if totalUnpaidCycles==0
    public CurrentCycle currentCycle;

    public List<Cycle> cycles = new ArrayList<>();
  }

  public static class CurrentCycle {
    public Integer index;              // 1-based cycle number
    public LocalDate startDate;
    public LocalDate endDate;          // null while open
    public Integer held;               // sessions held so far in this cycle
    public Integer present;
    public Integer absent;
    public Integer required;           // sessionsPerCycle
    public Integer chargeableSessions; // based on billAbsences flag
    public String  periodLabel;        // YearMonth of endDate (or today if open)
    public BigDecimal due;
    public BigDecimal paid;
    public BigDecimal balance;
    public String status;              // UNPAID | PENDING | PAID
    public String recognizeAt;         // START | END
  }

  public static class Cycle {
    public Integer cycleIndex; // 1-based
    public LocalDate startDate;
    public LocalDate endDate;
    public Integer heldSessions;
    public Integer presentCount;
    public Integer absentCount;
    public Integer chargedSessions;
    public String  periodLabel;   // YearMonth of endDate
    public BigDecimal amountDue;
    public BigDecimal amountPaid;
    public BigDecimal balance;
  }

  // getters / setters
  public Long getStudentId() { return studentId; }
  public void setStudentId(Long studentId) { this.studentId = studentId; }
  public String getStudentFullName() { return studentFullName; }
  public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }
  public int getTotalUnpaidCycles() { return totalUnpaidCycles; }
  public void setTotalUnpaidCycles(int totalUnpaidCycles) { this.totalUnpaidCycles = totalUnpaidCycles; }
  public List<Group> getGroups() { return groups; }
  public void setGroups(List<Group> groups) { this.groups = groups; }
}
