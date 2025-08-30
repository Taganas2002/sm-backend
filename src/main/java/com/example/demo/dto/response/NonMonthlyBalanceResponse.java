package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record NonMonthlyBalanceResponse(
  Long studentId,
  String studentFullName,
  String studentNumber,
  List<GroupBalance> groups
){
  public record GroupBalance(
    Long groupId,
    String groupName,
    String model,                // PER_SESSION | PER_HOUR
    Integer sessionsPurchased,   // for PER_SESSION
    Integer sessionsAttended,    // for PER_SESSION
    Integer sessionsRemaining,   // for PER_SESSION
    BigDecimal hoursPurchased,   // for PER_HOUR
    BigDecimal hoursAttended,    // for PER_HOUR
    BigDecimal hoursRemaining,   // for PER_HOUR
    BigDecimal unitPrice,        // session_cost or hourly_cost (from StudyGroup)
    String note                  // optional helper text
  ) {}
}
