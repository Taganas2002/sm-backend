// src/main/java/com/example/demo/controllers/BillingDuesController.java
package com.example.demo.controllers;

import com.example.demo.dto.response.*;
import com.example.demo.services.Interface.BillingDuesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/billing/dues")
public class BillingDuesController {

  private final BillingDuesService service;
  public BillingDuesController(BillingDuesService service) { this.service = service; }

  @GetMapping("/students/{studentId}/unpaid-monthly-groups")
  public StudentUnpaidGroupsResponse unpaidMonthlyGroups(
      @PathVariable Long studentId,
      @RequestParam(required = false, name = "period") String periodYYYYMM
  ) {
    return service.unpaidMonthlyGroups(studentId, periodYYYYMM);
  }

  @GetMapping("/monthly/search")
  public PageResponse<MonthlyDueRow> searchMonthly(
      @RequestParam String period,
      @RequestParam(required = false, defaultValue = "ALL") String status,
      @RequestParam(required = false) Long groupId,
      @RequestParam(required = false, name = "groupName") String groupNameLike,
      @RequestParam(required = false) String q,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "50") int size
  ) {
    return service.searchMonthly(period, status, groupId, groupNameLike, q, page, size);
  }

  // NEW: attendance-backed cycles (added recognizeAt; old params kept)
  @GetMapping("/students/{studentId}/unpaid-cycles")
  public StudentUnpaidCyclesResponse unpaidCycles(
      @PathVariable Long studentId,
      @RequestParam(required = false, defaultValue = "true")  Boolean billAbsences,
      @RequestParam(required = false, defaultValue = "100")   Integer limit,
      @RequestParam(required = false, defaultValue = "false") Boolean includeOpen,
      @RequestParam(required = false, defaultValue = "START") String recognizeAt // NEW
  ) {
    return service.unpaidSessionCycles(studentId, billAbsences, limit, includeOpen, recognizeAt);
  }
}
