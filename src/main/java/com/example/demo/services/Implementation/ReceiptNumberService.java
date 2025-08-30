package com.example.demo.services.Implementation;

import com.example.demo.repository.ReceiptRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
public class ReceiptNumberService {
  private final ReceiptRepo receiptRepo;
  public ReceiptNumberService(ReceiptRepo receiptRepo) { this.receiptRepo = receiptRepo; }

  @Transactional(readOnly = true)
  public String next() {
    YearMonth ym = YearMonth.now();
    String prefix = "RCPT-" + ym.toString().replace("-", "");
    long n = receiptRepo.countByReceiptNoStartingWith(prefix) + 1;
    return prefix + "-" + String.format("%05d", n);
  }
}
