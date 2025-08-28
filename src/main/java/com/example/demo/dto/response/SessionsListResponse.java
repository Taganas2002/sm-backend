package com.example.demo.dto.response;

import java.util.List;

public class SessionsListResponse {
  public List<Item> items;

  public static class Item {
    public Long sessionId;     // null if PLANNED-only
    public String date;        // yyyy-MM-dd
    public String startTime;   // HH:mm
    public String endTime;     // HH:mm
    public String status;      // PLANNED / OPEN / CLOSED
    public Integer presentCount; // null for planned
  }
}
