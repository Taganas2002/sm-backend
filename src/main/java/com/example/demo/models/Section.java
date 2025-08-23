package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "sections", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Section {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String name;

  // getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
}
