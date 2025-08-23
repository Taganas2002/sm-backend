package com.example.demo.dto.response;

public class ClassroomResponse {
  private Long id;
  private String roomName;
  private Integer capacity;
  private String equipment;
  private String notes;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getRoomName() { return roomName; }
  public void setRoomName(String roomName) { this.roomName = roomName; }
  public Integer getCapacity() { return capacity; }
  public void setCapacity(Integer capacity) { this.capacity = capacity; }
  public String getEquipment() { return equipment; }
  public void setEquipment(String equipment) { this.equipment = equipment; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}
