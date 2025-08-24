package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "subjects")
public class Subject {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 50)
  @Column(length = 50)
  private String code;

  @NotBlank
  @Size(max = 120)
  @Column(length = 120, nullable = false)
  private String name;

  @Lob
  private String notes;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }

  @Override public boolean equals(Object o){ return o instanceof Subject s && id!=null && id.equals(s.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
