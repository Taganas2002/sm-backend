package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubjectUpsertRequest {
  @NotBlank @Size(max = 120) private String name;
  @Size(max = 32) private String code;

  public String getName(){ return name; } public void setName(String name){ this.name = name; }
  public String getCode(){ return code; } public void setCode(String code){ this.code = code; }
}