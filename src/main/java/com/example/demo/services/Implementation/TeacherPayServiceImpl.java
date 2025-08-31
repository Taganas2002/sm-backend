// src/main/java/com/example/demo/services/Implementation/TeacherPayServiceImpl.java
package com.example.demo.services.Implementation;

import com.example.demo.dto.response.*;
import com.example.demo.models.*;
import com.example.demo.models.enums.*;
import com.example.demo.repository.*;
import com.example.demo.services.Interface.TeacherPayService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherPayServiceImpl implements TeacherPayService {

  private final TeacherEarningRepo earningRepo;
  private final TeacherPayoutRepo payoutRepo;
  private final StudyGroupRepo groupRepo;

  @PersistenceContext private EntityManager em;

  public TeacherPayServiceImpl(TeacherEarningRepo earningRepo,
                               TeacherPayoutRepo payoutRepo,
                               StudyGroupRepo groupRepo) {
    this.earningRepo = earningRepo;
    this.payoutRepo = payoutRepo;
    this.groupRepo = groupRepo;
  }

  private static BigDecimal z(BigDecimal v){ return v==null? BigDecimal.ZERO : v; }
  private static int zi(Integer v){ return v==null? 0 : v; }

  // ---- 1) Accrue on every student payment ----
  @Override
  public void accrueOnStudentPayment(StudentPayment sp) {
    StudyGroup g = sp.getGroup();
    if (g == null || g.getTeacher() == null) return;

    Teacher t = g.getTeacher();
    TeacherShareType type = g.getTeacherShareType();
    BigDecimal shareValue = z(g.getTeacherShareValue());

    // Determine base units for fixed models
    int sessionsUnits;
    if (sp.getPaymentType() == PaymentType.MONTHLY) {
      sessionsUnits = (sp.getSessionsPaid()!=null) ? sp.getSessionsPaid()
        : (g.getSessionsPerMonth()==null ? 0 : g.getSessionsPerMonth());
    } else if (sp.getPaymentType() == PaymentType.PER_SESSION) {
      sessionsUnits = zi(sp.getSessionsPaid());
    } else {
      sessionsUnits = 0;
    }
    BigDecimal hourUnits = (sp.getPaymentType()==PaymentType.PER_HOUR) ? z(sp.getHoursPaid()) : z(sp.getHoursPaid());

    BigDecimal baseAmount = z(sp.getAmountPaid());
    BigDecimal share = BigDecimal.ZERO;

    switch (type) {
      case NONE -> share = BigDecimal.ZERO;
      case PERCENT -> share = baseAmount.multiply(shareValue).divide(new BigDecimal("100"));
      case FIXED_PER_SESSION -> share = shareValue.multiply(new BigDecimal(sessionsUnits));
      case FIXED_PER_HOUR -> share = shareValue.multiply(hourUnits);
    }

    TeacherEarning e = new TeacherEarning();
    e.setTeacher(t);
    e.setGroup(g);
    e.setStudentPayment(sp);
    e.setRecognizedAt(sp.getPaymentDate()==null ? OffsetDateTime.now() : sp.getPaymentDate());
    e.setGrossAmount(baseAmount);
    e.setShareType(type);
    e.setShareValue(shareValue);
    e.setShareAmount(share);
    e.setStatus(EarningStatus.UNPAID);
    earningRepo.save(e);
  }

  // ---- 2) Summary ----
  @Transactional(readOnly = true)
  @Override
  public TeacherSummaryResponse summary(Long teacherId, Long groupId, OffsetDateTime from, OffsetDateTime to) {
    var unpaidTotal = earningRepo.sumUnpaid(teacherId, groupId, from, to);

    var rows = earningRepo.search(teacherId, groupId, from, to, null);
    Map<Long, List<TeacherEarning>> byGroup = rows.stream().collect(Collectors.groupingBy(e -> e.getGroup().getId()));

    TeacherSummaryResponse r = new TeacherSummaryResponse();
    r.teacherId = teacherId;
    r.teacherName = rows.stream().findFirst().map(e -> e.getTeacher().getFullName()).orElse(null);
    r.unpaidTotal = unpaidTotal;

    r.groups = byGroup.entrySet().stream().map(entry -> {
      var glist = entry.getValue();
      var g = glist.get(0).getGroup();
      var gr = new TeacherSummaryResponse.GroupRow();
      gr.groupId = g.getId();
      gr.groupName = g.getName();
      gr.shareType = g.getTeacherShareType().name();
      gr.period = rows.isEmpty()? null : YearMonth.from(rows.get(0).getRecognizedAt()).toString();
      BigDecimal accrued = glist.stream().map(TeacherEarning::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal unpaid  = glist.stream().filter(e -> e.getStatus()==EarningStatus.UNPAID)
                           .map(TeacherEarning::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
      gr.accrued = accrued; gr.unpaid = unpaid; gr.paid = accrued.subtract(unpaid);
      return gr;
    }).sorted(Comparator.comparing(gr -> gr.groupName==null? "" : gr.groupName.toLowerCase())).toList();

    return r;
  }

  // ---- 3) Earnings list ----
  @Transactional(readOnly = true)
  @Override
  public List<TeacherEarningRow> earnings(Long teacherId, String status, Long groupId, OffsetDateTime from, OffsetDateTime to) {
    EarningStatus st = null;
    if (status != null && !"ALL".equalsIgnoreCase(status) && !status.isBlank()) {
      st = EarningStatus.valueOf(status.toUpperCase());
    }
    return earningRepo.search(teacherId, groupId, from, to, st).stream().map(e -> {
      var row = new TeacherEarningRow();
      row.id = e.getId();
      row.recognizedAt = e.getRecognizedAt();
      row.groupId = e.getGroup().getId();
      row.groupName = e.getGroup().getName();
      row.grossAmount = e.getGrossAmount();
      row.shareType = e.getShareType().name();
      row.shareValue = e.getShareValue();
      row.shareAmount = e.getShareAmount();
      row.status = e.getStatus().name();
      row.studentPaymentId = e.getStudentPayment().getId();
      return row;
    }).toList();
  }

  // ---- 4) Create payout ----
  @Override
  public TeacherPayoutResponse createPayout(Long teacherId, List<Long> earningIds, String method, String reference, Long cashierUserId) {
    if (earningIds == null || earningIds.isEmpty()) throw new IllegalArgumentException("earningIds is required");

    var earnings = earningRepo.findByIdIn(earningIds);
    if (earnings.isEmpty()) throw new IllegalArgumentException("No earnings found");

    Teacher teacher = earnings.get(0).getTeacher();
    if (!Objects.equals(teacher.getId(), teacherId))
      throw new IllegalArgumentException("All earnings must belong to the same teacher");

    BigDecimal total = BigDecimal.ZERO;
    for (var e : earnings) {
      if (e.getStatus() != EarningStatus.UNPAID)
        throw new IllegalStateException("Earning " + e.getId() + " already paid");
      total = total.add(e.getShareAmount());
    }

    TeacherPayout p = new TeacherPayout();
    p.setPayoutNo(nextPayoutNo());
    p.setTeacher(teacher);
    p.setIssuedAt(OffsetDateTime.now());
    p.setMethod(method);
    p.setReference(reference);
    p.setTotalAmount(total);
    p.setCashierUserId(cashierUserId);
    p = payoutRepo.save(p);

    for (var e : earnings) {
      e.setStatus(EarningStatus.PAID);
      e.setPayout(p);
      earningRepo.save(e);
    }

    return toResponse(p, earnings);
  }

  // ---- 5) Read/List payouts ----
  @Transactional(readOnly = true)
  @Override
  public TeacherPayoutResponse readPayout(Long payoutId) {
    var p = payoutRepo.findById(payoutId).orElseThrow();
    var earnings = em.createQuery(
        "select e from TeacherEarning e where e.payout.id=:pid", TeacherEarning.class)
        .setParameter("pid", p.getId()).getResultList();
    return toResponse(p, earnings);
  }

  @Transactional(readOnly = true)
  @Override
  public List<TeacherPayoutResponse> listPayouts(Long teacherId) {
    var list = payoutRepo.findByTeacher_IdOrderByIssuedAtDesc(teacherId);
    return list.stream().map(p -> {
      var earnings = em.createQuery(
          "select e from TeacherEarning e where e.payout.id=:pid", TeacherEarning.class)
          .setParameter("pid", p.getId()).getResultList();
      return toResponse(p, earnings);
    }).toList();
  }

  // ---- helpers ----
  private TeacherPayoutResponse toResponse(TeacherPayout p, List<TeacherEarning> earnings) {
    var r = new TeacherPayoutResponse();
    r.payoutId = p.getId();
    r.payoutNo = p.getPayoutNo();
    r.teacherId = p.getTeacher().getId();
    r.teacherName = p.getTeacher().getFullName();
    r.issuedAt = p.getIssuedAt();
    r.method = p.getMethod();
    r.reference = p.getReference();
    r.totalAmount = p.getTotalAmount();
    r.cashierUserId = p.getCashierUserId();

    Map<Long, List<TeacherEarning>> byGroup = earnings.stream()
        .collect(Collectors.groupingBy(e -> e.getGroup().getId()));

    r.items = byGroup.entrySet().stream().map(en -> {
      var it = new TeacherPayoutResponse.Item();
      it.groupId = en.getKey();
      it.groupName = en.getValue().get(0).getGroup().getName();
      it.lines = en.getValue().size();
      it.amount = en.getValue().stream()
          .map(TeacherEarning::getShareAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      return it;
    }).sorted(Comparator.comparing(i -> i.groupName==null? "" : i.groupName.toLowerCase())).toList();

    return r;
  }
  
  @Override
  @Transactional
  public int rebuildFromPayments(Long teacherId, OffsetDateTime from, OffsetDateTime to) {
    // find payments for groups taught by teacher
    var payments = em.createQuery("""
        select sp from StudentPayment sp
        where sp.group.teacher.id = :tid
          and (:from is null or sp.paymentDate >= :from)
          and (:to   is null or sp.paymentDate <= :to)
        order by sp.paymentDate asc
        """, StudentPayment.class)
        .setParameter("tid", teacherId)
        .setParameter("from", from)
        .setParameter("to", to)
        .getResultList();

    int created = 0;
    for (var sp : payments) {
      boolean exists = earningRepo.existsByStudentPayment_Id(sp.getId());
      if (!exists) {
        accrueOnStudentPayment(sp);   // reuse your normal accrual
        created++;
      }
    }
    return created;
  }

  private String nextPayoutNo() {
    // simple unique generator; replace with your sequence service if you prefer
    String code;
    int guard = 0;
    do {
      code = "TPAY-" + YearMonth.now() + "-" + UUID.randomUUID().toString().substring(0,6).toUpperCase();
      guard++;
    } while (payoutRepo.existsByPayoutNo(code) && guard < 5);
    return code;
  }
}
