// src/main/java/com/example/demo/services/Implementation/BillingDuesServiceImpl.java
package com.example.demo.services.Implementation;

import com.example.demo.dto.response.*;
import com.example.demo.models.*;
import com.example.demo.models.enums.*;
import com.example.demo.repository.*;
import com.example.demo.services.Interface.BillingDuesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.*;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional(readOnly = true)
public class BillingDuesServiceImpl implements BillingDuesService {

  private final StudentRepo studentRepo;
  private final EnrollmentRepo enrollmentRepo;
  private final StudentPaymentRepo paymentRepo;
  private final JdbcTemplate jdbc;
  private final StudentAttendanceRepo attendanceRepo;

  @PersistenceContext private EntityManager em;

  public BillingDuesServiceImpl(StudentRepo studentRepo,
                                EnrollmentRepo enrollmentRepo,
                                StudentPaymentRepo paymentRepo,
                                JdbcTemplate jdbc,
                                StudentAttendanceRepo attendanceRepo) {
    this.studentRepo = studentRepo;
    this.enrollmentRepo = enrollmentRepo;
    this.paymentRepo = paymentRepo;
    this.jdbc = jdbc;
    this.attendanceRepo = attendanceRepo;
  }

  // ---------------- existing APIs unchanged ----------------
  @Override
  public StudentUnpaidGroupsResponse unpaidMonthlyGroups(Long studentId, String periodYYYYMM) {
    if (studentId == null) throw new ResponseStatusException(BAD_REQUEST, "studentId is required");
    YearMonth ym;
    try {
      ym = (periodYYYYMM == null || periodYYYYMM.isBlank())
           ? YearMonth.now()
           : YearMonth.parse(periodYYYYMM);
    } catch (Exception e) {
      throw new ResponseStatusException(BAD_REQUEST, "period must be YYYY-MM");
    }

    Student student = studentRepo.findById(studentId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Student not found"));

    var enrollments = enrollmentRepo.findByStudent_IdAndStatus(studentId, EnrollmentStatus.ACTIVE);

    StudentUnpaidGroupsResponse resp = new StudentUnpaidGroupsResponse();
    resp.setStudentId(student.getId());
    resp.setStudentFullName(student.getFullName());
    resp.setPeriod(ym.toString());

    for (Enrollment e : enrollments) {
      StudyGroup g = e.getGroup();
      if (g == null || g.getBillingModel() != BillingModel.MONTHLY) continue;

      BigDecimal due = monthlyDueForGroup(g);
      BigDecimal paid = paymentRepo.sumMonthlyPaid(studentId, g.getId(), ym.toString());
      BigDecimal balance = due.subtract(paid == null ? BigDecimal.ZERO : paid);

      if (balance.signum() > 0) {
        var item = new StudentUnpaidGroupsResponse.Item();
        item.setGroupId(g.getId());
        item.setGroupName(g.getName());
        item.setModel("MONTHLY");
        item.setAmountDue(due);
        item.setAmountPaid(paid == null ? BigDecimal.ZERO : paid);
        item.setBalance(balance);
        resp.getGroups().add(item);
      }
    }
    return resp;
  }

  private BigDecimal monthlyDueForGroup(StudyGroup g) {
    if (g.getMonthlyFee() != null && g.getMonthlyFee().signum() > 0) return g.getMonthlyFee();
    int sessions = g.getSessionsPerMonth() == null ? 0 : g.getSessionsPerMonth();
    BigDecimal sessionCost = g.getSessionCost() == null ? BigDecimal.ZERO : g.getSessionCost();
    return sessionCost.multiply(BigDecimal.valueOf(sessions));
  }

  private static class RowMap implements RowMapper<MonthlyDueRow> {
    @Override public MonthlyDueRow mapRow(ResultSet rs, int rowNum) throws SQLException {
      Long studentId = rs.getLong("student_id");
      String fullName = rs.getString("full_name");
      String phone = rs.getString("phone");
      Long groupId = rs.getLong("group_id");
      String groupName = rs.getString("group_name");
      String period = rs.getString("period");
      BigDecimal due = rs.getBigDecimal("due_amount");
      BigDecimal paid = rs.getBigDecimal("paid_amount");
      BigDecimal balance = rs.getBigDecimal("balance_amount");
      String status;
      int cmp = balance.compareTo(BigDecimal.ZERO);
      if (cmp <= 0) status = "PAID";
      else if (paid.signum() > 0 && paid.compareTo(due) < 0) status = "PARTIAL";
      else status = "UNPAID";
      return new MonthlyDueRow(studentId, fullName, phone, groupId, groupName, period, due, paid, balance, status);
    }
  }

  @Override
  public PageResponse<MonthlyDueRow> searchMonthly(
      String period, String status, Long groupId, String groupNameLike, String q, int page, int size) {
    if (!StringUtils.hasText(period) || !period.matches("\\d{4}-\\d{2}")) {
      throw new IllegalArgumentException("period must be YYYY-MM");
    }
    if (!StringUtils.hasText(status)) status = "ALL";
    status = status.toUpperCase();

    String baseSelect =
        """
        select
          s.id as student_id, s.full_name, s.phone,
          g.id as group_id, g.name as group_name,
          ? as period,
          coalesce(g.monthly_fee, coalesce(g.session_cost,0) * coalesce(g.sessions_per_month,0)) as due_amount,
          coalesce(p.paid, 0) as paid_amount,
          (coalesce(g.monthly_fee, coalesce(g.session_cost,0) * coalesce(g.sessions_per_month,0)) - coalesce(p.paid,0)) as balance_amount
        from enrollments e
        join students s on s.id = e.student_id
        join class_groups g on g.id = e.group_id and g.billing_model = 'MONTHLY'
        left join (
          select student_id, group_id, coalesce(sum(amount_paid),0) as paid
          from student_payments
          where payment_type = 'MONTHLY' and month_year = ?
          group by student_id, group_id
        ) p on p.student_id = s.id and p.group_id = g.id
        where e.status = 'ACTIVE'
        """;

    String baseCount =
        """
        select count(*)
        from enrollments e
        join students s on s.id = e.student_id
        join class_groups g on g.id = e.group_id and g.billing_model = 'MONTHLY'
        left join (
          select student_id, group_id, coalesce(sum(amount_paid),0) as paid
          from student_payments
          where payment_type = 'MONTHLY' and month_year = ?
          group by student_id, group_id
        ) p on p.student_id = s.id and p.group_id = g.id
        where e.status = 'ACTIVE'
        """;

    List<Object> args = new ArrayList<>(List.of(period, period));
    List<Object> countArgs = new ArrayList<>(List.of(period));
    StringBuilder where = new StringBuilder();

    if (groupId != null) { where.append(" and g.id = ? "); args.add(groupId); countArgs.add(groupId); }
    if (StringUtils.hasText(groupNameLike)) {
      where.append(" and lower(g.name) like lower(?) ");
      String like = "%" + groupNameLike.trim() + "%";
      args.add(like); countArgs.add(like);
    }
    if (StringUtils.hasText(q)) {
      where.append(" and (lower(s.full_name) like lower(?) or s.phone like ?) ");
      String like = "%" + q.trim() + "%";
      args.add(like); args.add(like);
      countArgs.add(like); countArgs.add(like);
    }

    switch (status) {
      case "PAID"    -> where.append(" and (coalesce(g.monthly_fee, coalesce(g.session_cost,0) * coalesce(g.sessions_per_month,0)) - coalesce(p.paid,0)) <= 0 ");
      case "UNPAID"  -> where.append(" and (coalesce(g.monthly_fee, coalesce(g.session_cost,0) * coalesce(g.sessions_per_month,0)) - coalesce(p.paid,0))  > 0 and coalesce(p.paid,0)=0 ");
      case "PARTIAL" -> where.append(" and coalesce(p.paid,0) > 0 and coalesce(p.paid,0) < coalesce(g.monthly_fee, coalesce(g.session_cost,0) * coalesce(g.sessions_per_month,0)) ");
      case "ALL"     -> {}
      default -> throw new IllegalArgumentException("status must be one of: PAID | UNPAID | PARTIAL | ALL");
    }

    String order = " order by lower(s.full_name) asc, g.name asc ";
    int safePage = Math.max(page, 0);
    int safeSize = (size <= 0 || size > 200) ? 50 : size;
    int offset = safePage * safeSize;

    String sql = baseSelect + where + order + " limit ? offset ? ";
    args.add(safeSize); args.add(offset);
    String countSql = baseCount + where;

    long total = jdbc.queryForObject(countSql, countArgs.toArray(), Long.class);
    List<MonthlyDueRow> rows = jdbc.query(sql, args.toArray(), new RowMap());

    return new PageResponse<>(rows, safePage, safeSize, total);
  }

  // ---------------- NEW: attendance-backed cycles with recognizeAt + currentCycle ----------------
  @Override
  public StudentUnpaidCyclesResponse unpaidSessionCycles(Long studentId,
                                                         Boolean billAbsences,
                                                         Integer limitCycles,
                                                         Boolean includeOpen,
                                                         String recognizeAtRaw) {

    if (studentId == null) throw new ResponseStatusException(BAD_REQUEST, "studentId is required");
    boolean chargeAbsences = (billAbsences == null) || billAbsences;
    int cap = (limitCycles == null || limitCycles <= 0 || limitCycles > 200) ? 100 : limitCycles;
    boolean includeTrailing = Boolean.TRUE.equals(includeOpen);

    String recognizeAt = (recognizeAtRaw == null ? "START" : recognizeAtRaw.trim().toUpperCase());
    boolean recogStart = !"END".equals(recognizeAt); // default START
    boolean recogEnd   =  "END".equals(recognizeAt);

    Student student = studentRepo.findById(studentId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Student not found"));

    var enrollments = enrollmentRepo.findByStudent_IdAndStatus(studentId, EnrollmentStatus.ACTIVE);

    StudentUnpaidCyclesResponse out = new StudentUnpaidCyclesResponse();
    out.setStudentId(student.getId());
    out.setStudentFullName(student.getFullName());

    int totalUnpaid = 0;

    for (Enrollment e : enrollments) {
      StudyGroup g = e.getGroup();
      if (g == null || g.getBillingModel() != BillingModel.MONTHLY) continue;

      Integer N = g.getSessionsPerMonth();
      if (N == null || N <= 0) continue;

      // boundary start: max(enrollmentDate, group.startDate)
      LocalDate startFrom = e.getEnrollmentDate();
      if (g.getStartDate() != null && (startFrom == null || g.getStartDate().isAfter(startFrom))) {
        startFrom = g.getStartDate();
      }

      // fetch sessions for this group
      List<AttendanceSession> sessions;
      if (startFrom != null) {
        sessions = em.createQuery(
              "select s from AttendanceSession s where s.group.id=:gid and s.sessionDate>=:from order by s.sessionDate asc",
              AttendanceSession.class)
            .setParameter("gid", g.getId())
            .setParameter("from", startFrom)
            .getResultList();
      } else {
        sessions = em.createQuery(
              "select s from AttendanceSession s where s.group.id=:gid order by s.sessionDate asc",
              AttendanceSession.class)
            .setParameter("gid", g.getId())
            .getResultList();
      }
      if (sessions.isEmpty()) continue;

      // load attendance for these sessions and this student
      List<Long> sessionIds = sessions.stream().map(AttendanceSession::getId).toList();
      var allRows = attendanceRepo.findBySession_IdIn(sessionIds);
      Map<Long, StudentAttendanceStatus> statusBySession = new HashMap<>();
      for (var a : allRows) {
        if (a.getStudent()!=null && Objects.equals(a.getStudent().getId(), studentId)) {
          statusBySession.put(a.getSession().getId(), a.getStatus());
        }
      }

      StudentUnpaidCyclesResponse.Group gDto = new StudentUnpaidCyclesResponse.Group();
      gDto.groupId = g.getId();
      gDto.groupName = g.getName();
      gDto.sessionsPerCycle = N;

      int idx = 0, pushed = 0, cycleNo = 1;
      while (idx < sessions.size()) {
        int remaining = sessions.size() - idx;
        int take = Math.min(N, remaining);
        var block = sessions.subList(idx, idx + take);

        boolean fullCycle = (take == N);
        boolean isOpenCycle = !fullCycle && (remaining == take); // trailing block

        LocalDate startDate = block.get(0).getSessionDate();
        LocalDate endDate   = block.get(block.size()-1).getSessionDate();
        // label = month of end date (works with current repo sumMonthlyPaid)
        String label = YearMonth.from(endDate).toString();

        int held = block.size();
        int present = 0, absent = 0;
        for (var s : block) {
          var st = statusBySession.get(s.getId());
          if (st == StudentAttendanceStatus.PRESENT) present++;
          else if (st == StudentAttendanceStatus.ABSENT) absent++;
        }

        int charged = chargeAbsences ? N : present;

        BigDecimal due = (g.getMonthlyFee()!=null && g.getMonthlyFee().signum()>0)
            ? g.getMonthlyFee()
            : (g.getSessionCost()==null ? BigDecimal.ZERO
               : g.getSessionCost().multiply(BigDecimal.valueOf(charged)));

        BigDecimal paid = Optional.ofNullable(
            paymentRepo.sumMonthlyPaid(studentId, g.getId(), label)
        ).orElse(BigDecimal.ZERO);

        BigDecimal balance = due.subtract(paid);

        // Include in "unpaid cycles" list?
        boolean includeThis =
            (fullCycle && balance.signum() > 0) ||
            (isOpenCycle && includeTrailing && recogStart && balance.signum() > 0);

        if (includeThis) {
          var c = new StudentUnpaidCyclesResponse.Cycle();
          c.cycleIndex = cycleNo;
          c.startDate = startDate;
          c.endDate = endDate;
          c.heldSessions = held;
          c.presentCount = present;
          c.absentCount = absent;
          c.chargedSessions = charged;
          c.periodLabel = label;
          c.amountDue = due;
          c.amountPaid = paid;
          c.balance = balance;
          gDto.cycles.add(c);
          totalUnpaid++;
          pushed++;
          if (pushed >= cap) break;
        }

        // Build currentCycle progress (always fill when this is the trailing, open block)
        if (isOpenCycle) {
          StudentUnpaidCyclesResponse.CurrentCycle cur = new StudentUnpaidCyclesResponse.CurrentCycle();
          cur.index = cycleNo;
          cur.startDate = startDate;
          cur.endDate = null;
          cur.held = held;
          cur.present = present;
          cur.absent = absent;
          cur.required = N;
          cur.chargeableSessions = charged;
          cur.periodLabel = label;
          cur.due = due;
          cur.paid = paid;
          cur.balance = balance;
          cur.recognizeAt = recognizeAt;
          if (recogEnd) {
            cur.status = "PENDING"; // only recognize when cycle completes
          } else {
            cur.status = (balance.signum() > 0) ? "UNPAID" : "PAID";
          }
          gDto.currentCycle = cur;
        }

        idx += fullCycle ? N : take;
        cycleNo++;
      }

      if (gDto.currentCycle == null) {
        // All cycles were full; reflect last full as "no open cycle" but still fine.
      }

      if (gDto.currentCycle != null || !gDto.cycles.isEmpty()) {
        out.getGroups().add(gDto);
      }
    }

    out.setTotalUnpaidCycles(totalUnpaid);
    return out;
  }
}
