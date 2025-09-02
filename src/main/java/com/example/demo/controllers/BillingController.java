package com.example.demo.controllers;

import com.example.demo.dto.request.CollectPaymentRequest;
import com.example.demo.dto.response.ReceiptResponse;
import com.example.demo.services.Interface.BillingCollectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/billing")
public class BillingController {

  private final BillingCollectService service;
  public BillingController(BillingCollectService service) { this.service = service; }

  // Collect a payment (monthly / per-session / per-hour)
  @PostMapping("/collect")
  public ReceiptResponse collect(@RequestBody CollectPaymentRequest req,
                                 @RequestHeader(value = "X-Cashier-UserId", required = false) Long cashierUserId) {
    return service.collect(req, cashierUserId);
  }

  // Read one receipt
  @GetMapping("/receipts/{id}")
  public ReceiptResponse read(@PathVariable Long id) {
    return service.readReceipt(id);
  }

  // All receipts for student (history)
  @GetMapping("/students/{studentId}/receipts")
  public List<ReceiptResponse> history(@PathVariable Long studentId) {
    return service.history(studentId);
  }
}
