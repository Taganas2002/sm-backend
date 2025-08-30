package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record ReceiptResponse(
    Long receiptId,
    String receiptNo,
    OffsetDateTime issuedAt,
    String method,
    School school,
    Student student,
    Cashier cashier,
    List<Line> lines,
    BigDecimal total
) {
  public static record School(String name, String logoUrl) {}
  public static record Student(Long id, String fullName, String number) {}
  public static record Cashier(Long id, String name) {}
  public static record Line(
      String groupName,
      String model,        // MONTHLY | PER_SESSION | PER_HOUR
      String period,       // YYYY-MM
      Integer sessions,    // per-session
      BigDecimal hours,    // per-hour
      BigDecimal amount,   // paid on this line
      BigDecimal balanceAfter
  ) {}
}
