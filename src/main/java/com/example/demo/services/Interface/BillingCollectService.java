package com.example.demo.services.Interface;

import com.example.demo.dto.request.CollectPaymentRequest;
import com.example.demo.dto.response.ReceiptResponse;

import java.util.List;

public interface BillingCollectService {
  ReceiptResponse collect(CollectPaymentRequest req, Long cashierUserId);
  ReceiptResponse readReceipt(Long receiptId);
  List<ReceiptResponse> history(Long studentId);
}
