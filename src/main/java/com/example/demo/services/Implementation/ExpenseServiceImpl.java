package com.example.demo.services.Implementation;

import com.example.demo.dto.request.ExpenseUpsertRequest;
import com.example.demo.dto.response.*;
import com.example.demo.models.Expense;
import com.example.demo.repository.ExpenseRepo;
import com.example.demo.services.Interface.ExpenseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

  private final ExpenseRepo repo;
  private final JdbcTemplate jdbc;

  @PersistenceContext private EntityManager em;

  public ExpenseServiceImpl(ExpenseRepo repo, JdbcTemplate jdbc) {
    this.repo = repo; this.jdbc = jdbc;
  }

  private ExpenseResponse toDto(Expense e){
    ExpenseResponse r = new ExpenseResponse();
    r.setId(e.getId());
    r.setSchoolId(e.getSchoolId());
    r.setCategory(e.getCategory());
    r.setSubCategory(e.getSubCategory());
    r.setVendor(e.getVendor());
    r.setReference(e.getReference());
    r.setMethod(e.getMethod());
    r.setExpenseDate(e.getExpenseDate());
    r.setAmount(e.getAmount());
    r.setTaxAmount(e.getTaxAmount());
    r.setTotalAmount(e.getTotalAmount());
    r.setNotes(e.getNotes());
    return r;
  }

  private void apply(Expense e, ExpenseUpsertRequest req){
    e.setSchoolId(req.getSchoolId());
    e.setCategory(req.getCategory());
    e.setSubCategory(req.getSubCategory());
    e.setVendor(req.getVendor());
    e.setReference(req.getReference());
    e.setMethod(req.getMethod());
    e.setExpenseDate(req.getExpenseDate()==null? LocalDate.now(): req.getExpenseDate());
    e.setAmount(req.getAmount());
    e.setTaxAmount(req.getTaxAmount()==null? BigDecimal.ZERO : req.getTaxAmount());
    e.setTotalAmount(e.getAmount()==null? BigDecimal.ZERO : e.getAmount().add(e.getTaxAmount()==null? BigDecimal.ZERO : e.getTaxAmount()));
    e.setNotes(req.getNotes());
  }

  @Override
  public ExpenseResponse create(ExpenseUpsertRequest req) {
    Expense e = new Expense(); apply(e, req);
    return toDto(repo.save(e));
  }

  @Override
  public ExpenseResponse update(Long id, ExpenseUpsertRequest req) {
    Expense e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Expense not found"));
    apply(e, req);
    return toDto(repo.save(e));
  }

  @Transactional(readOnly = true)
  @Override
  public ExpenseResponse get(Long id) {
    return toDto(repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Expense not found")));
  }

  @Override
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Expense not found");
    repo.deleteById(id);
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<ExpenseResponse> search(String q, String category, LocalDate from, LocalDate to,
                                              Double min, Double max, Pageable pageable) {
    Specification<Expense> spec = Specification.where(null);

    if (StringUtils.hasText(q)) {
      String like = "%"+q.toLowerCase().trim()+"%";
      spec = spec.and((root, cq, cb) -> cb.or(
          cb.like(cb.lower(root.get("vendor")), like),
          cb.like(cb.lower(root.get("reference")), like),
          cb.like(cb.lower(root.get("notes")), like)
      ));
    }
    if (StringUtils.hasText(category)) {
      spec = spec.and((root, cq, cb) -> cb.equal(root.get("category"), category));
    }
    if (from != null) spec = spec.and((r,q1,cb)-> cb.greaterThanOrEqualTo(r.get("expenseDate"), from));
    if (to   != null) spec = spec.and((r,q1,cb)-> cb.lessThan(r.get("expenseDate"), to));
    if (min  != null) spec = spec.and((r,q1,cb)-> cb.ge(r.get("totalAmount"), min));
    if (max  != null) spec = spec.and((r,q1,cb)-> cb.le(r.get("totalAmount"), max));

    Pageable p = pageable==null ? PageRequest.of(0, 50, Sort.by("expenseDate").descending()) : pageable;
    Page<Expense> page = repo.findAll(spec, p);

    return new PageResponse<>(page.map(this::toDto).getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
  }

  @Transactional(readOnly = true)
  @Override
  public ExpenseSummaryResponse summary(LocalDate from, LocalDate to) {
    ExpenseSummaryResponse r = new ExpenseSummaryResponse();
    r.setFrom(from); r.setTo(to);
    r.setTotal(repo.sumTotal(from, to));

    var rows = repo.sumByCategory(from, to);
    r.setByCategory(rows.stream()
        .map(o -> new ExpenseCategoryTotal((String)o[0], (BigDecimal)o[1]))
        .toList());
    return r;
  }

  @Transactional(readOnly = true)
  @Override
  public ProfitAndLossResponse profitAndLoss(LocalDate from, LocalDate to) {
    // Build WHERE dynamically for student_payments
    StringBuilder incSql = new StringBuilder("select coalesce(sum(amount_paid),0) from student_payments where 1=1");
    List<Object> incArgs = new ArrayList<>();
    if (from != null) { incSql.append(" and date(payment_date) >= ?"); incArgs.add(from); }
    if (to   != null) { incSql.append(" and date(payment_date)   < ?"); incArgs.add(to); }
    BigDecimal income = jdbc.queryForObject(incSql.toString(), incArgs.toArray(), BigDecimal.class);

    // teacher_payouts
    StringBuilder paySql = new StringBuilder("select coalesce(sum(total_amount),0) from teacher_payouts where 1=1");
    List<Object> payArgs = new ArrayList<>();
    if (from != null) { paySql.append(" and date(issued_at) >= ?"); payArgs.add(from); }
    if (to   != null) { paySql.append(" and date(issued_at)   < ?"); payArgs.add(to); }
    BigDecimal teacherCost = jdbc.queryForObject(paySql.toString(), payArgs.toArray(), BigDecimal.class);

    BigDecimal expenses = repo.sumTotal(from, to);
    BigDecimal net = income.subtract(teacherCost).subtract(expenses);

    ProfitAndLossResponse r = new ProfitAndLossResponse();
    r.setFrom(from); r.setTo(to);
    r.setIncome(income);
    r.setTeacherCost(teacherCost);
    r.setExpenses(expenses);
    r.setNet(net);
    return r;
  }
}
