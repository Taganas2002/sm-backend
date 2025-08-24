package com.example.demo.dto.response;

public class SubjectResponse {
  private Long id;
  private String code;
  private String name;
  private String notes;

  public Long getId(){ return id; } public void setId(Long v){ this.id = v; }
  public String getCode(){ return code; } public void setCode(String v){ this.code = v; }
  public String getName(){ return name; } public void setName(String v){ this.name = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }
}
