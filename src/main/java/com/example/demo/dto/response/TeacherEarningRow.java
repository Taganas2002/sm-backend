// src/main/java/com/example/demo/dto/response/TeacherEarningRow.java
package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TeacherEarningRow {
  public Long id;
  public OffsetDateTime recognizedAt;
  public Long groupId;
  public String groupName;
  public BigDecimal grossAmount;
  public String shareType;
  public BigDecimal shareValue;
  public BigDecimal shareAmount;
  public String status; // UNPAID/PAID
  public Long studentPaymentId;
}
