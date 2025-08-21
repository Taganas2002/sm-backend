package com.example.demo.dto.response;

import java.util.List;

public class AccountSummaryDto {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private List<String> roles;

  public AccountSummaryDto() {}
  public AccountSummaryDto(Long id, String name, String email, String phone, List<String> roles) {
    this.id = id; this.name = name; this.email = email; this.phone = phone; this.roles = roles;
  }

  public Long getId() { return id; }  public void setId(Long id) { this.id = id; }
  public String getName() { return name; } public void setName(String name) { this.name = name; }
  public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
  public List<String> getRoles() { return roles; } public void setRoles(List<String> roles) { this.roles = roles; }
}
