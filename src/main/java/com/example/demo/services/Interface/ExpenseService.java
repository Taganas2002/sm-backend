package com.example.demo.services.Interface;

import com.example.demo.dto.request.ExpenseUpsertRequest;
import com.example.demo.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ExpenseService {
  ExpenseResponse create(ExpenseUpsertRequest req);
  ExpenseResponse update(Long id, ExpenseUpsertRequest req);
  ExpenseResponse get(Long id);
  void delete(Long id);

  PageResponse<ExpenseResponse> search(
      String q, String category,
      LocalDate from, LocalDate to,
      Double min, Double max,
      Pageable pageable
  );

  ExpenseSummaryResponse summary(LocalDate from, LocalDate to);

  ProfitAndLossResponse profitAndLoss(LocalDate from, LocalDate to);
}
