package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubjectUpsertRequest {
  @Size(max = 50)  private String code;
  @NotBlank @Size(max = 120) private String name;
  private String notes;

  public String getCode(){ return code; } public void setCode(String v){ this.code = v; }
  public String getName(){ return name; } public void setName(String v){ this.name = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }
}
