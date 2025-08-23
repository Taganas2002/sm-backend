package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SchoolUpsertRequest {
  @NotBlank @Size(max = 160) private String name;
  @Size(max = 255) private String address;
  @Size(max = 60)  private String phone;
  @Size(max = 160) private String email;
  private Boolean active = true;

  public String getName(){ return name; }  public void setName(String name){ this.name = name; }
  public String getAddress(){ return address; } public void setAddress(String address){ this.address = address; }
  public String getPhone(){ return phone; } public void setPhone(String phone){ this.phone = phone; }
  public String getEmail(){ return email; } public void setEmail(String email){ this.email = email; }
  public Boolean getActive(){ return active; } public void setActive(Boolean active){ this.active = active; }
}
