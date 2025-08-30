package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StudentUnpaidGroupsResponse {
  private Long studentId;
  private String studentFullName;
  private String period; // YYYY-MM
  private List<Item> groups = new ArrayList<>();

  public static class Item {
    private Long groupId;
    private String groupName;
    private String model;          // "MONTHLY"
    private BigDecimal amountDue;  // cycle due
    private BigDecimal amountPaid; // sum of payments this cycle
    private BigDecimal balance;    // due - paid (only > 0 are returned)

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
  }

  public Long getStudentId() { return studentId; }
  public void setStudentId(Long studentId) { this.studentId = studentId; }
  public String getStudentFullName() { return studentFullName; }
  public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }
  public String getPeriod() { return period; }
  public void setPeriod(String period) { this.period = period; }
  public List<Item> getGroups() { return groups; }
  public void setGroups(List<Item> groups) { this.groups = groups; }
}
