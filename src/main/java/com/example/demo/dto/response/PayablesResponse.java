package com.example.demo.dto.response;

import java.util.List;

public record PayablesResponse(
    Long studentId,
    String studentFullName,
    List<UnpaidCyclesResponse.GroupCycles> monthly, // unpaid MONTHLY cycles
    List<NonMonthlyDuesRow> nonMonthly              // zero-balance PER_SESSION / PER_HOUR
) {}
