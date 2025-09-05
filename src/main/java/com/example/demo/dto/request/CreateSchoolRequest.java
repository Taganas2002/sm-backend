package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSchoolRequest {

  @NotBlank @Size(max = 200)
  private String schoolName;

  @Email @Size(max = 255)
  private String schoolEmail;

  @Size(max = 40)
  private String schoolPhone;

  @Size(max = 255)
  private String schoolAddress;

  // getters/setters
  public String getSchoolName() { return schoolName; }
  public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
  public String getSchoolEmail() { return schoolEmail; }
  public void setSchoolEmail(String schoolEmail) { this.schoolEmail = schoolEmail; }
  public String getSchoolPhone() { return schoolPhone; }
  public void setSchoolPhone(String schoolPhone) { this.schoolPhone = schoolPhone; }
  public String getSchoolAddress() { return schoolAddress; }
  public void setSchoolAddress(String schoolAddress) { this.schoolAddress = schoolAddress; }
}
