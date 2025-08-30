package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class CollectPaymentRequest {
  private Long studentId;
  private String method;
  private String reference;
  private String notes;
  private Boolean printReceipt;
  private List<Item> items;

  public static class Item {
    private Long groupId;          // REQUIRED
    private String model;          // MONTHLY | PER_SESSION | PER_HOUR
    private String period;         // YYYY-MM when model=MONTHLY
    private Integer sessions;      // when model=PER_SESSION
    private BigDecimal hours;      // when model=PER_HOUR
    private BigDecimal amount;     // REQUIRED

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
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

  public Long getStudentId() { return studentId; }
  public void setStudentId(Long studentId) { this.studentId = studentId; }
  public String getMethod() { return method; }
  public void setMethod(String method) { this.method = method; }
  public String getReference() { return reference; }
  public void setReference(String reference) { this.reference = reference; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
  public Boolean getPrintReceipt() { return printReceipt; }
  public void setPrintReceipt(Boolean printReceipt) { this.printReceipt = printReceipt; }
  public List<Item> getItems() { return items; }
  public void setItems(List<Item> items) { this.items = items; }
}
