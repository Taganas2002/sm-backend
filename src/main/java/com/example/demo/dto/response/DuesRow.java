package com.example.demo.dto.response;

import java.math.BigDecimal;
public record DuesRow(
  String rowId,
  Long studentId,
  String studentFullName,
  String studentNumber,
  Long groupId,
  String groupName,
  String period,                // YYYY-MM
  Integer sessionsPerCycle,
  Integer sessionsHeld,
  Integer attended,
  Integer absent,
  BigDecimal sessionPrice,
  BigDecimal amountDue,
  BigDecimal amountPaid,
  BigDecimal balance,
  String status,
  Integer unpaidOlderCycles
) {}
