package com.example.demo.dto.response;

import java.math.BigDecimal;

public record NonMonthlyDuesRow(
    Long studentId,
    String studentFullName,
    String studentNumber,
    Long groupId,
    String groupName,
    String model,                 // PER_SESSION | PER_HOUR
    Integer purchasedSessions,    // for PER_SESSION
    Integer attendedSessions,     // for PER_SESSION
    Integer remainingSessions,    // for PER_SESSION
    BigDecimal purchasedHours,    // for PER_HOUR
    BigDecimal attendedHours,     // for PER_HOUR
    BigDecimal remainingHours,    // for PER_HOUR
    BigDecimal unitPrice,         // session_cost or hourly_cost
    String status                 // NO_BALANCE | LOW | OK
) {}
