package com.example.demo.services.Implementation;

import com.example.demo.dto.response.*;
import com.example.demo.models.enums.AbsenceCalcBasis;
import com.example.demo.repository.BillingQueryDao;
import com.example.demo.services.Interface.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

@Service
public class BillingCalcServiceImpl implements BillingCalcService {

  private final BillingQueryDao q;
  public BillingCalcServiceImpl(BillingQueryDao q){ this.q = q; }

  // ---- policy for monthly (defaults come from DAO) ----
  record Policy(boolean billAbsences, AbsenceCalcBasis basis, boolean oldestFirst){}

  private Policy resolvePolicy(Long studentId, Long groupId){
    var p = q.schoolPolicyForGroup(groupId);
    boolean billAbs = (Boolean)p.get("bill_absences_monthly");
    AbsenceCalcBasis basis = AbsenceCalcBasis.valueOf((String)p.get("absence_calc_basis"));
    boolean oldest = (Boolean)p.get("enforce_oldest_cycle_first");
    return new Policy(billAbs, basis, oldest);
  }

  // ---- MONTHLY due row ----
  @Override
  public DuesRow buildMonthlyRow(Long studentId, Long groupId, YearMonth ym,
                                 String studentName, String studentNumber, String groupName){
    var policy = resolvePolicy(studentId, groupId);

    int held = q.sessionsHeld(groupId, ym);
    int attended = q.attendedInMonth(studentId, groupId, ym);
    int required = q.sessionsPerCycle(groupId);
    int absent = Math.max(0, ((policy.basis()==AbsenceCalcBasis.REQUIRED)? required : held) - attended);

    BigDecimal pricePerSession = q.sessionCost(groupId);
    BigDecimal monthlyFee = q.monthlyFee(groupId);
    int chargeSessions = policy.basis()==AbsenceCalcBasis.REQUIRED ? required : held;
    int qty = policy.billAbsences() ? chargeSessions : attended;

    // amount due: monthly fee OR per-session * qty
    BigDecimal due = monthlyFee.signum()>0 ? monthlyFee : pricePerSession.multiply(BigDecimal.valueOf(qty));
    BigDecimal paid = q.sumMonthlyPayments(studentId, groupId, ym);
    BigDecimal balance = due.subtract(paid);

    String status;
    if (balance.signum() <= 0) {
      status = (paid.signum() > 0 || due.signum() == 0) ? "PAID" : "UNPAID";
    } else {
      status = (paid.signum() > 0) ? "PARTIAL" : "UNPAID";
    }

    // IMPORTANT: if the group has NO price configured (due == 0) and no payment recorded,
    // still surface the month as UNPAID so the UI shows the group (your requirement).
    if (due.signum() == 0 && paid.signum() == 0 && required > 0) {
      status = "UNPAID";
      balance = BigDecimal.ZERO; // amount unknown, but state is unpaid
    }

    List<String> older = policy.oldestFirst()
        ? q.unpaidOlderPeriods(studentId, groupId, ym, policy.billAbsences(), policy.basis())
        : List.of();

    return new DuesRow(
        studentId + "-" + groupId + "-" + ym,
        studentId, studentName, studentNumber,
        groupId, groupName,
        ym.toString(),
        required, held, attended, absent,
        pricePerSession, due, paid, balance, status,
        older.size()
    );
  }

  // ---- MONTHLY unpaid cycles (modal) ----
  @Override
  public UnpaidCyclesResponse unpaidCycles(Long studentId, List<Long> groupIdsOrNull){
    var st = q.studentInfo(studentId).orElseThrow();
    String sName = (String) st.get("full_name");
    String sNum  = (String) st.getOrDefault("student_number", null);

    List<Long> groupIds = (groupIdsOrNull!=null && !groupIdsOrNull.isEmpty())
        ? groupIdsOrNull
        : new ArrayList<>(new HashSet<>(q.getJdbc().queryForList(
            "select group_id from enrollments where student_id=?", Long.class, studentId)));

    List<UnpaidCyclesResponse.GroupCycles> groups = new ArrayList<>();

    for (Long gid : groupIds) {
      var policy = resolvePolicy(studentId, gid);
      String gName = q.groupName(gid);
      YearMonth start = q.firstCycleForStudentInGroup(studentId, gid);
      YearMonth cur = YearMonth.now();

      List<UnpaidCyclesResponse.Cycle> cycles = new ArrayList<>();
      for (YearMonth ym = start; !ym.isAfter(cur); ym = ym.plusMonths(1)) {
        var row = buildMonthlyRow(studentId, gid, ym, sName, sNum, gName);
        if (!"PAID".equals(row.status())) {
          boolean mustFirst = policy.oldestFirst()
              && !q.unpaidOlderPeriods(studentId, gid, ym, policy.billAbsences(), policy.basis()).isEmpty();
          cycles.add(new UnpaidCyclesResponse.Cycle(
              ym.toString(), row.sessionsPerCycle(), row.attended(), row.absent(),
              row.sessionPrice(), row.amountDue(), row.amountPaid(), row.balance(), row.status(), mustFirst
          ));
        }
      }
      if (!cycles.isEmpty())
        groups.add(new UnpaidCyclesResponse.GroupCycles(gid, gName, cycles));
    }

    return new UnpaidCyclesResponse(studentId, sName, sNum,
        new UnpaidCyclesResponse.School("", ""), groups);
  }

  // ---- PER_SESSION / PER_HOUR balances (per student) ----
  @Override
  public NonMonthlyBalanceResponse nonMonthlyBalances(Long studentId){
    var st = q.studentInfo(studentId).orElseThrow();
    String sName = (String) st.get("full_name");
    String sNum  = (String) st.getOrDefault("student_number", null);

    var rows = q.getJdbc().queryForList(
      "select g.id as group_id, g.name as group_name, g.billing_model " +
      "from class_groups g join enrollments e on e.group_id=g.id " +
      "where e.student_id=? and g.billing_model in ('PER_SESSION','PER_HOUR')", studentId);

    List<NonMonthlyBalanceResponse.GroupBalance> out = new ArrayList<>();

    for (var r : rows){
      Long gid = ((Number)r.get("group_id")).longValue();
      String gname = (String) r.get("group_name");
      String model = (String) r.get("billing_model");

      if ("PER_SESSION".equals(model)){
        int attended = q.attendedOverall(studentId, gid);
        int purchased = q.sumPackPayments(studentId, gid).intValue();
        int remaining = Math.max(0, purchased - attended);
        out.add(new NonMonthlyBalanceResponse.GroupBalance(
            gid, gname, "PER_SESSION",
            purchased, attended, remaining,
            null, null, null,
            q.sessionCost(gid), "Session packs balance"
        ));
      } else {
        BigDecimal attendedHours = q.hoursOverall(studentId, gid);
        BigDecimal purchasedHours = q.sumHourPayments(studentId, gid);
        BigDecimal remaining = purchasedHours.subtract(attendedHours);
        if (remaining.signum() < 0) remaining = BigDecimal.ZERO;
        out.add(new NonMonthlyBalanceResponse.GroupBalance(
            gid, gname, "PER_HOUR",
            null, null, null,
            purchasedHours, attendedHours, remaining,
            q.hourlyCost(gid), "Hours credit balance"
        ));
      }
    }
    return new NonMonthlyBalanceResponse(studentId, sName, sNum, out);
  }

  // ---- NON-MONTHLY dues (list) ----
  @Override
  public List<NonMonthlyDuesRow> nonMonthlyDues(Long groupId,
                                                String status,
                                                String qtext,
                                                Integer thresholdSessions,
                                                BigDecimal thresholdHours) {

    var rows = q.getJdbc().queryForList(
      "select e.student_id, s.full_name, g.id as group_id, g.name as group_name, g.billing_model " +
      "from enrollments e " +
      "join students s on s.id = e.student_id " +
      "join class_groups g on g.id = e.group_id " +
      "where g.billing_model in ('PER_SESSION','PER_HOUR') " +
      (groupId != null ? " and g.id="+groupId : ""));

    int sessThresh = (thresholdSessions == null ? 0 : thresholdSessions);
    BigDecimal hourThresh = (thresholdHours == null ? BigDecimal.ZERO : thresholdHours);

    List<NonMonthlyDuesRow> out = new ArrayList<>();

    for (var r : rows) {
      Long sid = ((Number) r.get("student_id")).longValue();
      String sName = (String) r.get("full_name");
      Long gid = ((Number) r.get("group_id")).longValue();
      String gName = (String) r.get("group_name");
      String model = (String) r.get("billing_model");

      if (qtext != null && !qtext.isBlank()) {
        String needle = qtext.toLowerCase();
        if (!(sName.toLowerCase().contains(needle) || gName.toLowerCase().contains(needle))) {
          continue;
        }
      }

      if ("PER_SESSION".equals(model)) {
        int purchased = q.sumPackPayments(sid, gid).intValue();
        int attended  = q.attendedOverall(sid, gid);
        int remaining = Math.max(0, purchased - attended);

        String st = remaining <= 0 ? "NO_BALANCE" : (remaining <= sessThresh ? "LOW" : "OK");
        if (!"ANY".equalsIgnoreCase(status) && !st.equalsIgnoreCase(status)) continue;

        out.add(new NonMonthlyDuesRow(
            sid, sName, null, gid, gName, "PER_SESSION",
            purchased, attended, remaining,
            null, null, null,
            q.sessionCost(gid), st
        ));
      } else {
        BigDecimal purchased = q.sumHourPayments(sid, gid);
        BigDecimal attended  = q.hoursOverall(sid, gid);
        BigDecimal remaining = purchased.subtract(attended);
        if (remaining.signum() < 0) remaining = BigDecimal.ZERO;

        String st;
        if (remaining.compareTo(BigDecimal.ZERO) == 0) st = "NO_BALANCE";
        else if (remaining.compareTo(hourThresh) <= 0) st = "LOW";
        else st = "OK";

        if (!"ANY".equalsIgnoreCase(status) && !st.equalsIgnoreCase(status)) continue;

        out.add(new NonMonthlyDuesRow(
            sid, sName, null, gid, gName, "PER_HOUR",
            null, null, null,
            purchased, attended, remaining,
            q.hourlyCost(gid), st
        ));
      }
    }

    out.sort(Comparator.comparing(NonMonthlyDuesRow::status)
                       .thenComparing(NonMonthlyDuesRow::studentFullName, Comparator.nullsLast(String::compareTo)));
    return out;
  }

  // ---- what to show when clicking a student ----
  @Override
  public PayablesResponse payables(Long studentId, Long groupIdOrNull){
    var st = q.studentInfo(studentId).orElseThrow();

    // Monthly (unpaid cycles only — now also includes zero-price cycles with no payment)
    var monthly = unpaidCycles(studentId, groupIdOrNull==null? null : List.of(groupIdOrNull)).groups();

    // Non-monthly: show packs/hours where remaining == 0 (not paid)
    var nm = nonMonthlyBalances(studentId).groups().stream()
        .filter(g ->
            ("PER_SESSION".equals(g.model()) && (g.sessionsRemaining()!=null && g.sessionsRemaining()==0))
         || ("PER_HOUR".equals(g.model())   && (g.hoursRemaining()!=null && g.hoursRemaining().compareTo(BigDecimal.ZERO)==0))
        )
        .map(g -> new NonMonthlyDuesRow(
            studentId, (String)st.get("full_name"), null,
            g.groupId(), g.groupName(), g.model(),
            g.sessionsPurchased(), g.sessionsAttended(), g.sessionsRemaining(),
            g.hoursPurchased(), g.hoursAttended(), g.hoursRemaining(),
            g.unitPrice(), "NO_BALANCE"
        ))
        .toList();

    return new PayablesResponse(studentId, (String)st.get("full_name"), monthly, nm);
  }
}
