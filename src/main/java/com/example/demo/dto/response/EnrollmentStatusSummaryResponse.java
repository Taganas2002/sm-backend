package com.example.demo.dto.response;

public class EnrollmentStatusSummaryResponse {
  private Long groupId;
  private Integer capacity;         // may be null
  private long active;
  private long suspended;
  private long dropped;
  private long completed;
  private long total;
  private Integer capacityLeft;     // may be null

  public Long getGroupId() { return groupId; }
  public void setGroupId(Long groupId) { this.groupId = groupId; }
  public Integer getCapacity() { return capacity; }
  public void setCapacity(Integer capacity) { this.capacity = capacity; }
  public long getActive() { return active; }
  public void setActive(long active) { this.active = active; }
  public long getSuspended() { return suspended; }
  public void setSuspended(long suspended) { this.suspended = suspended; }
  public long getDropped() { return dropped; }
  public void setDropped(long dropped) { this.dropped = dropped; }
  public long getCompleted() { return completed; }
  public void setCompleted(long completed) { this.completed = completed; }
  public long getTotal() { return total; }
  public void setTotal(long total) { this.total = total; }
  public Integer getCapacityLeft() { return capacityLeft; }
  public void setCapacityLeft(Integer capacityLeft) { this.capacityLeft = capacityLeft; }
}
