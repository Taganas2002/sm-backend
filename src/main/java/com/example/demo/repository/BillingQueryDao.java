package com.example.demo.repository;

import com.example.demo.models.enums.AbsenceCalcBasis;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Repository
public class BillingQueryDao {

  private final JdbcTemplate jdbc;
  public BillingQueryDao(JdbcTemplate jdbc){ this.jdbc = jdbc; }
  public JdbcTemplate getJdbc(){ return jdbc; }

  // -------- StudyGroup meta (class_groups) --------
  public Map<String,Object> groupInfo(Long groupId){
    return jdbc.queryForMap(
      "select g.id, g.name as group_name, g.school_id, g.billing_model, " +
      "g.sessions_per_month, g.monthly_fee, g.session_cost, g.hourly_cost, g.session_duration_min " +
      "from class_groups g where g.id = ?", groupId);
  }
  public String groupName(Long groupId){
    return jdbc.query("select name from class_groups where id=?",
        rs -> rs.next()? rs.getString(1):null, groupId);
  }
  public int sessionsPerCycle(Long groupId){
    Integer n = jdbc.query("select sessions_per_month from class_groups where id=?",
        rs -> rs.next()? rs.getInt(1):0, groupId);
    return n == null? 0 : n;
  }
  public BigDecimal monthlyFee(Long groupId){
    return jdbc.query("select coalesce(monthly_fee,0) from class_groups where id=?",
        rs -> rs.next()? rs.getBigDecimal(1): BigDecimal.ZERO, groupId);
  }
  public BigDecimal sessionCost(Long groupId){
    return jdbc.query("select coalesce(session_cost,0) from class_groups where id=?",
        rs -> rs.next()? rs.getBigDecimal(1): BigDecimal.ZERO, groupId);
  }
  public BigDecimal hourlyCost(Long groupId){
    return jdbc.query("select coalesce(hourly_cost,0) from class_groups where id=?",
        rs -> rs.next()? rs.getBigDecimal(1): BigDecimal.ZERO, groupId);
  }

  // -------- Student --------
  public Optional<Map<String,Object>> studentInfo(Long studentId){
    var list = jdbc.queryForList("select id, full_name from students where id = ?", studentId);
    if (list.isEmpty()) return Optional.empty();
    var m = new HashMap<>(list.get(0));
    m.putIfAbsent("student_number", null); // keep callers happy
    return Optional.of(m);
  }

  // -------- Enrollment lookups (robust for different schemas) --------
  public List<Long> enrolledGroupIdsForStudent(Long studentId){
    List<Long> ids = new ArrayList<>();
    try {
      ids = jdbc.queryForList(
          "select group_id from enrollments where student_id=? and (status is null or status in ('ENROLLED','ACTIVE'))",
          Long.class, studentId);
    } catch (Exception ignore) {}
    if (ids.isEmpty()) try {
      ids = jdbc.queryForList("select group_id from student_groups where student_id=?", Long.class, studentId);
    } catch (Exception ignore) {}
    if (ids.isEmpty()) try {
      ids = jdbc.queryForList("select group_id from group_students where student_id=?", Long.class, studentId);
    } catch (Exception ignore) {}
    return ids;
  }

  public List<Map.Entry<Long,Long>> monthlyStudentGroupPairs(Long groupId){
    String sql = "select e.student_id, e.group_id from enrollments e " +
                 "join class_groups g on g.id=e.group_id where g.billing_model='MONTHLY'";
    if (groupId != null) sql += " and e.group_id=" + groupId;

    List<Map<String,Object>> rows;
    try { rows = jdbc.queryForList(sql); }
    catch (Exception e) { rows = List.of(); }

    List<Map.Entry<Long,Long>> out = new ArrayList<>();
    for (var r: rows) {
      out.add(Map.entry(((Number)r.get("student_id")).longValue(),
                        ((Number)r.get("group_id")).longValue()));
    }
    return out;
  }

  // -------- Attendance aggregates --------
  public int sessionsHeld(Long groupId, YearMonth ym){
    LocalDate from = ym.atDay(1), to = ym.atEndOfMonth();
    Integer n = jdbc.query(
      "select count(*) from attendance_sessions s where s.group_id=? and s.session_date between ? and ?",
      rs -> { rs.next(); return rs.getInt(1); }, groupId, from, to);
    return n == null? 0 : n;
  }
  public int attendedInMonth(Long studentId, Long groupId, YearMonth ym){
    LocalDate from = ym.atDay(1), to = ym.atEndOfMonth();
    Integer n = jdbc.query(
      "select count(*) from attendance_records ar " +
      "join attendance_sessions s on s.id = ar.attendance_session_id " +
      "where ar.student_id=? and s.group_id=? and ar.status='PRESENT' and s.session_date between ? and ?",
      rs -> { rs.next(); return rs.getInt(1); }, studentId, groupId, from, to);
    return n == null? 0 : n;
  }
  public int attendedOverall(Long studentId, Long groupId){
    Integer n = jdbc.query(
      "select count(*) from attendance_records ar " +
      "join attendance_sessions s on s.id = ar.attendance_session_id " +
      "where ar.student_id=? and s.group_id=? and ar.status='PRESENT'",
      rs -> { rs.next(); return rs.getInt(1); }, studentId, groupId);
    return n == null? 0 : n;
  }
  public BigDecimal hoursOverall(Long studentId, Long groupId){
    BigDecimal v = jdbc.query(
      "select coalesce(sum(ar.hours_attended),0) from attendance_records ar " +
      "join attendance_sessions s on s.id = ar.attendance_session_id " +
      "where ar.student_id=? and s.group_id=? and ar.status='PRESENT'",
      rs -> { rs.next(); return rs.getBigDecimal(1); }, studentId, groupId);
    return v==null? BigDecimal.ZERO : v;
  }

  // -------- Policies --------
  public Map<String,Object> schoolPolicyForGroup(Long groupId){
    Map<String,Object> m = new HashMap<>();
    m.put("bill_absences_monthly", true);
    m.put("absence_calc_basis", "REQUIRED");
    m.put("enforce_oldest_cycle_first", true);
    return m;
  }

  // -------- Cycles / payments --------
  public YearMonth firstCycleForStudentInGroup(Long studentId, Long groupId){
    LocalDate d = jdbc.query(
      "select coalesce(g.start_date, current_date) from class_groups g where g.id=?",
      rs -> rs.next()? rs.getObject(1, LocalDate.class) : null, groupId);
    return d==null? YearMonth.now(): YearMonth.from(d);
  }
  public BigDecimal sumMonthlyPayments(Long studentId, Long groupId, YearMonth ym){
    BigDecimal v = jdbc.query(
      "select coalesce(sum(amount_paid),0) from student_payments " +
      "where student_id=? and group_id=? and payment_type='MONTHLY' and month_year=?",
      rs -> { rs.next(); return rs.getBigDecimal(1); }, studentId, groupId, ym.toString());
    return v==null? BigDecimal.ZERO : v;
  }
  public BigDecimal sumPackPayments(Long studentId, Long groupId){
    BigDecimal v = jdbc.query(
      "select coalesce(sum(sessions_paid),0) from student_payments " +
      "where student_id=? and group_id=? and payment_type='PER_SESSION'",
      rs -> { rs.next(); return rs.getBigDecimal(1); }, studentId, groupId);
    return v==null? BigDecimal.ZERO : v;
  }
  public BigDecimal sumHourPayments(Long studentId, Long groupId){
    BigDecimal v = jdbc.query(
      "select coalesce(sum(hours_paid),0) from student_payments " +
      "where student_id=? and group_id=? and payment_type='PER_HOUR'",
      rs -> { rs.next(); return rs.getBigDecimal(1); }, studentId, groupId);
    return v==null? BigDecimal.ZERO : v;
  }

  public List<String> unpaidOlderPeriods(Long studentId, Long groupId, YearMonth currentYm,
                                         boolean chargeAbsences, AbsenceCalcBasis basis){
    List<String> result = new ArrayList<>();
    YearMonth start = firstCycleForStudentInGroup(studentId, groupId);
    for (YearMonth ym = start; ym.isBefore(currentYm); ym = ym.plusMonths(1)) {
      BigDecimal bal = cycleBalance(studentId, groupId, ym, chargeAbsences, basis);
      if (bal.signum() > 0) result.add(ym.toString());
    }
    return result;
  }

  public BigDecimal cycleBalance(Long studentId, Long groupId, YearMonth ym,
                                 boolean chargeAbsences, AbsenceCalcBasis basis){
    int held = sessionsHeld(groupId, ym);
    int attended = attendedInMonth(studentId, groupId, ym);
    int required = sessionsPerCycle(groupId);

    int chargeSessions = (basis==AbsenceCalcBasis.REQUIRED) ? required : held;
    int qty = chargeAbsences ? chargeSessions : attended;

    BigDecimal due = monthlyFee(groupId).signum()>0
        ? monthlyFee(groupId)
        : sessionCost(groupId).multiply(BigDecimal.valueOf(qty));

    BigDecimal paid = sumMonthlyPayments(studentId, groupId, ym);
    return due.subtract(paid);
  }
}
