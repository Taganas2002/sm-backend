package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(
  name = "classrooms",
  uniqueConstraints = @UniqueConstraint(columnNames = "room_name") // optional, but handy
)
public class Classroom {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "room_name", nullable = false, length = 120)
  private String roomName;              // اسم القاعة / الرقم

  @Column(name = "capacity")
  private Integer capacity;             // الطاقة الاستيعابية

  @Column(name = "equipment", columnDefinition = "TEXT")
  private String equipment;             // التجهيزات (سبورة، بروجيكتور...)

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;                 // ملاحظات

  // getters/setters
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
