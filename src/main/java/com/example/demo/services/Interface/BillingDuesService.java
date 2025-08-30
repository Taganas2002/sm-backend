// src/main/java/com/example/demo/services/Interface/BillingDuesService.java
package com.example.demo.services.Interface;

import com.example.demo.dto.response.*;

public interface BillingDuesService {

  StudentUnpaidGroupsResponse unpaidMonthlyGroups(Long studentId, String periodYYYYMM);

  PageResponse<MonthlyDueRow> searchMonthly(
      String period, String status, Long groupId, String groupNameLike, String q, int page, int size);

  // add recognizeAt
  StudentUnpaidCyclesResponse unpaidSessionCycles(
      Long studentId, Boolean billAbsences, Integer limitCycles, Boolean includeOpen, String recognizeAt);
}
