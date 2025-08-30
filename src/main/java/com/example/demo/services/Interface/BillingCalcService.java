package com.example.demo.services.Interface;

import com.example.demo.dto.response.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface BillingCalcService {
  DuesRow buildMonthlyRow(Long studentId, Long groupId, YearMonth ym,
                          String studentName, String studentNumber, String groupName);

  UnpaidCyclesResponse unpaidCycles(Long studentId, List<Long> groupIdsOrNull);

  NonMonthlyBalanceResponse nonMonthlyBalances(Long studentId);

  List<NonMonthlyDuesRow> nonMonthlyDues(Long groupId, String status,
                                         String qtext, Integer thresholdSessions,
                                         BigDecimal thresholdHours);

  // NEW: what the UI shows when you click a student
  PayablesResponse payables(Long studentId, Long groupIdOrNull);
}
