package com.example.demo.dto.response;

public class BulkMarkResponse {
  public Long sessionId;
  public Long groupId;
  public int requested;     // how many ids sent
  public int newlyAdded;    // new rows inserted as PRESENT
  public int alreadyPresent; // duplicates
  public int rejected;      // NOT_ENROLLED rejections
  public int presentCount;  // total PRESENT in that session
}
