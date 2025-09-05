package com.example.demo.dto.request;

public class CreateBranchRequest {
  private String schoolName; private String schoolEmail; private String schoolPhone; private String schoolAddress;

  public String getSchoolName() { return schoolName; } public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
  public String getSchoolEmail() { return schoolEmail; } public void setSchoolEmail(String schoolEmail) { this.schoolEmail = schoolEmail; }
  public String getSchoolPhone() { return schoolPhone; } public void setSchoolPhone(String schoolPhone) { this.schoolPhone = schoolPhone; }
  public String getSchoolAddress() { return schoolAddress; } public void setSchoolAddress(String schoolAddress) { this.schoolAddress = schoolAddress; }
}
