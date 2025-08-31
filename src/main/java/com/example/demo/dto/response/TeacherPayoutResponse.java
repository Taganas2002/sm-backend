// src/main/java/com/example/demo/dto/response/TeacherPayoutResponse.java
package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class TeacherPayoutResponse {
  public Long payoutId;
  public String payoutNo;
  public Long teacherId;
  public String teacherName;
  public OffsetDateTime issuedAt;
  public String method;
  public String reference;
  public BigDecimal totalAmount;
  public Long cashierUserId;

  public List<Item> items;

  public static class Item {
    public Long groupId;
    public String groupName;
    public int lines;
    public BigDecimal amount;
  }
}
