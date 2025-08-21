package com.example.demo.dto.response;

import java.util.List;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private String phone;
  private List<String> roles;
  private List<String> authorities; // NEW

  public JwtResponse(String accessToken, Long id, String username, String email, String phone,
                     List<String> roles, List<String> authorities) {
    this.token = accessToken; this.id = id; this.username = username;
    this.email = email; this.phone = phone; this.roles = roles; this.authorities = authorities;
  }

  public String getToken() { return token; } public void setToken(String token) { this.token = token; }
  public String getType() { return type; }
  public Long getId() { return id; }
  public String getUsername() { return username; }
  public String getEmail() { return email; }
  public String getPhone() { return phone; }
  public List<String> getRoles() { return roles; }
  public List<String> getAuthorities() { return authorities; }
}
