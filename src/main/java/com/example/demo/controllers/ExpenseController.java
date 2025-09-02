package com.example.demo.controllers;

import com.example.demo.dto.request.ExpenseUpsertRequest;
import com.example.demo.dto.response.*;
import com.example.demo.services.Interface.ExpenseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/accounting")
public class ExpenseController {

  private final ExpenseService svc;
  public ExpenseController(ExpenseService svc){ this.svc = svc; }

  @PostMapping("/expenses")
  public ExpenseResponse create(@RequestBody ExpenseUpsertRequest req){ return svc.create(req); }

  @PutMapping("/expenses/{id}")
  public ExpenseResponse update(@PathVariable Long id, @RequestBody ExpenseUpsertRequest req){ return svc.update(id, req); }

  @GetMapping("/expenses/{id}")
  public ExpenseResponse get(@PathVariable Long id){ return svc.get(id); }

  @DeleteMapping("/expenses/{id}")
  public void delete(@PathVariable Long id){ svc.delete(id); }

  @GetMapping("/expenses")
  public PageResponse<ExpenseResponse> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String from,
      @RequestParam(required = false) String to,
      @RequestParam(required = false) Double min,
      @RequestParam(required = false) Double max,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "50") int size
  ) {
    Pageable p = PageRequest.of(Math.max(page,0), Math.min(Math.max(size,1), 200));
    return svc.search(
        q, category,
        from==null? null : LocalDate.parse(from),
        to==null? null : LocalDate.parse(to),
        min, max, p
    );
  }

  @GetMapping("/expenses/summary")
  public ExpenseSummaryResponse summary(
      @RequestParam(required = false) String from,
      @RequestParam(required = false) String to
  ){
    return svc.summary(
        from==null? null : LocalDate.parse(from),
        to==null? null : LocalDate.parse(to)
    );
  }

  @GetMapping("/reports/profit-loss")
  public ProfitAndLossResponse pnl(
      @RequestParam(required = false) String from,
      @RequestParam(required = false) String to
  ) {
    return svc.profitAndLoss(
        from==null? null : LocalDate.parse(from),
        to==null? null : LocalDate.parse(to)
    );
  }
}
