package com.example.demo.dto.response;

public class RunningConsumptionDto {
  private Long studentId;
  private Long groupId;
  private int attended;
  private int quota;
  private String ratio;
  private int cyclesCompleted;
  private int currentCycleAttended;
  private int remainingInCurrentCycle;
  private boolean needsPayment;

  public Long getStudentId() { return studentId; }
  public void setStudentId(Long studentId) { this.studentId = studentId; }
  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }
  public int getAttended() { return attended; }
  public void setAttended(int attended) { this.attended = attended; }
  public int getQuota() { return quota; }
  public void setQuota(int quota) { this.quota = quota; }
  public String getRatio() { return ratio; }
  public void setRatio(String ratio) { this.ratio = ratio; }
  public int getCyclesCompleted() { return cyclesCompleted; }
  public void setCyclesCompleted(int cyclesCompleted) { this.cyclesCompleted = cyclesCompleted; }
  public int getCurrentCycleAttended() { return currentCycleAttended; }
  public void setCurrentCycleAttended(int currentCycleAttended) { this.currentCycleAttended = currentCycleAttended; }
  public int getRemainingInCurrentCycle() { return remainingInCurrentCycle; }
  public void setRemainingInCurrentCycle(int remainingInCurrentCycle) { this.remainingInCurrentCycle = remainingInCurrentCycle; }
  public boolean isNeedsPayment() { return needsPayment; }
  public void setNeedsPayment(boolean needsPayment) { this.needsPayment = needsPayment; }
}
