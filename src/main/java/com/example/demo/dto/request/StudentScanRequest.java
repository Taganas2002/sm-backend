package com.example.demo.dto.request;

public class StudentScanRequest {
  public Long groupId;           // required
  public Long studentId;         // or
  public String studentCardUid;  // one of these two must be provided
}
