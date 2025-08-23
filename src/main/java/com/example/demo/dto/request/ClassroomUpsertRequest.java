package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClassroomUpsertRequest {

  @NotBlank
  @Size(max = 120)
  private String roomName;

  private Integer capacity;     // nullable on purpose

  @Size(max = 65535)            // TEXT
  private String equipment;

  @Size(max = 65535)            // TEXT
  private String notes;

  // getters/setters
  public String getRoomName() { return roomName; }
  public void setRoomName(String roomName) { this.roomName = roomName; }
  public Integer getCapacity() { return capacity; }
  public void setCapacity(Integer capacity) { this.capacity = capacity; }
  public String getEquipment() { return equipment; }
  public void setEquipment(String equipment) { this.equipment = equipment; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}
