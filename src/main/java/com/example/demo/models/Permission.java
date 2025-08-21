package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 120)
  private String code;         // e.g. MENU:TEACHERS_VIEW, API:STUDENTS_READ

  private String description;  // optional

  public Permission() {}
  public Permission(Long id, String code, String description) {
    this.id = id; this.code = code; this.description = description;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
