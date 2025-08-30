package com.example.demo.dto.response;

import java.math.BigDecimal;

public record MonthlyDueRow(
    Long studentId,
    String studentFullName,
    String phone,
    Long groupId,
    String groupName,
    String period,          // YYYY-MM
    BigDecimal due,
    BigDecimal paid,
    BigDecimal balance,     // due - paid
    String status           // PAID | PARTIAL | UNPAID
) {}
