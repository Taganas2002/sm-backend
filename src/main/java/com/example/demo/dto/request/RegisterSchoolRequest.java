package com.example.demo.dto.request;

public class RegisterSchoolRequest {
  // admin account
  private String username; private String email; private String phone; private String password;
  // school
  private String schoolName; private String schoolEmail; private String schoolPhone; private String schoolAddress;

  // getters/setters
  public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
  public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
  public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
  public String getSchoolName() { return schoolName; } public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
  public String getSchoolEmail() { return schoolEmail; } public void setSchoolEmail(String schoolEmail) { this.schoolEmail = schoolEmail; }
  public String getSchoolPhone() { return schoolPhone; } public void setSchoolPhone(String schoolPhone) { this.schoolPhone = schoolPhone; }
  public String getSchoolAddress() { return schoolAddress; } public void setSchoolAddress(String schoolAddress) { this.schoolAddress = schoolAddress; }
}
