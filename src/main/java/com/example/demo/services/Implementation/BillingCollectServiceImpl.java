package com.example.demo.services.Implementation;

import com.example.demo.dto.request.CollectPaymentRequest;
import com.example.demo.dto.response.ReceiptResponse;
import com.example.demo.models.Student;
import com.example.demo.models.StudyGroup;
import com.example.demo.models.Receipt;
import com.example.demo.models.ReceiptLine;
import com.example.demo.models.StudentPayment;
import com.example.demo.models.enums.PaymentType;
import com.example.demo.repository.StudentRepo;
import com.example.demo.repository.StudyGroupRepo;
import com.example.demo.repository.ReceiptRepo;
import com.example.demo.repository.StudentPaymentRepo;
import com.example.demo.services.Interface.BillingCollectService;
import com.example.demo.services.Implementation.*;
import com.example.demo.services.Interface.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
public class BillingCollectServiceImpl implements BillingCollectService {

  private final ReceiptRepo receiptRepo;
  private final StudentPaymentRepo paymentRepo;
  private final StudentRepo studentRepo;
  private final StudyGroupRepo groupRepo;
  private final ReceiptNumberService receiptNumberService;
  private final TeacherPayService teacherPayService;

//constructor
public BillingCollectServiceImpl(ReceiptRepo receiptRepo,
                                StudentPaymentRepo paymentRepo,
                                StudentRepo studentRepo,
                                StudyGroupRepo groupRepo,
                                ReceiptNumberService receiptNumberService,
                                TeacherPayService teacherPayService) {
 this.receiptRepo = receiptRepo;
 this.paymentRepo = paymentRepo;
 this.studentRepo = studentRepo;
 this.groupRepo = groupRepo;
 this.receiptNumberService = receiptNumberService;
 this.teacherPayService = teacherPayService;   // << add this
}

  private static String up(String s){ return s==null? "" : s.trim().toUpperCase(); }
  private static BigDecimal z(BigDecimal v){ return v==null? BigDecimal.ZERO : v; }
  private static int zi(Integer v){ return v==null? 0 : v; }

  /** Compute list price for the line based on StudyGroup pricing (fallback to amount). */
  private BigDecimal computeDue(StudyGroup g, CollectPaymentRequest.Item it){
    String model = up(it.getModel());
    if ("MONTHLY".equals(model)) {
      BigDecimal monthly = z(g.getMonthlyFee());
      if (monthly.signum() > 0) return monthly;
      int spm = g.getSessionsPerMonth()==null? 0 : g.getSessionsPerMonth();
      return z(g.getSessionCost()).multiply(BigDecimal.valueOf(spm)).max(z(it.getAmount()));
    }
    if ("PER_SESSION".equals(model)) {
      return z(g.getSessionCost()).multiply(BigDecimal.valueOf(zi(it.getSessions()))).max(z(it.getAmount()));
    }
    if ("PER_HOUR".equals(model)) {
      return z(g.getHourlyCost()).multiply(z(it.getHours())).max(z(it.getAmount()));
    }
    return z(it.getAmount());
  }

  @Override
  public ReceiptResponse collect(CollectPaymentRequest req, Long cashierUserId) {
    if (req == null || req.getStudentId() == null || CollectionUtils.isEmpty(req.getItems()))
      throw new ResponseStatusException(BAD_REQUEST, "studentId and items are required");

    Student student = studentRepo.findById(req.getStudentId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Student not found"));

    // validate & total
    BigDecimal total = BigDecimal.ZERO;
    for (CollectPaymentRequest.Item it : req.getItems()) {
      if (it.getGroupId() == null) throw new ResponseStatusException(BAD_REQUEST, "groupId is required");
      if (it.getAmount() == null || it.getAmount().compareTo(BigDecimal.ZERO) < 0)
        throw new ResponseStatusException(BAD_REQUEST, "amount must be >= 0");
      String model = up(it.getModel());
      if ("MONTHLY".equals(model) && (it.getPeriod()==null || it.getPeriod().isBlank()))
        throw new ResponseStatusException(BAD_REQUEST, "period (YYYY-MM) is required for MONTHLY");
      if ("PER_SESSION".equals(model) && (it.getSessions()==null || it.getSessions()<=0))
        throw new ResponseStatusException(BAD_REQUEST, "sessions must be > 0 for PER_SESSION");
      if ("PER_HOUR".equals(model) && (it.getHours()==null || it.getHours().compareTo(BigDecimal.ZERO)<=0))
        throw new ResponseStatusException(BAD_REQUEST, "hours must be > 0 for PER_HOUR");

      total = total.add(it.getAmount());
    }

    // build receipt
    Receipt r = new Receipt();
    r.setReceiptNo(receiptNumberService.next());
    r.setIssuedAt(OffsetDateTime.now());
    r.setMethod(up(req.getMethod()));
    r.setReference(req.getReference());
    r.setStudent(student);
    r.setCashierUserId(cashierUserId);
    r.setTotalAmount(total);

    List<ReceiptResponse.Line> outLines = new ArrayList<>();

    for (CollectPaymentRequest.Item it : req.getItems()) {
      StudyGroup g = groupRepo.findById(it.getGroupId())
          .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Group not found: "+it.getGroupId()));

      // line on receipt
      ReceiptLine line = new ReceiptLine();
      line.setGroup(g);
      line.setModel(up(it.getModel()));
      line.setPeriod(it.getPeriod());
      line.setSessions(it.getSessions());
      line.setHours(it.getHours());
      line.setAmount(it.getAmount());
      r.addLine(line);

      // student payment (and BALANCE!)
      StudentPayment sp = new StudentPayment();
      sp.setStudent(student);
      sp.setGroup(g);
      sp.setPaymentType(PaymentType.valueOf(up(it.getModel())));
      sp.setMonthYear(it.getPeriod());
      sp.setSessionsPaid(it.getSessions());
      sp.setHoursPaid(it.getHours());
      sp.setAmountPaid(it.getAmount());
      BigDecimal due = computeDue(g, it);
      sp.setAmountDue(due);
      sp.setBalance(due.subtract(z(it.getAmount()))); // 🔹 REQUIRED: stops NOT NULL error
      sp.setPaymentDate(r.getIssuedAt());
      paymentRepo.save(sp);
      teacherPayService.accrueOnStudentPayment(sp);

      outLines.add(new ReceiptResponse.Line(
          g.getName(), up(it.getModel()), it.getPeriod(),
          it.getSessions(), it.getHours(), it.getAmount(), null
      ));
    }

    r = receiptRepo.save(r);

    return new ReceiptResponse(
        r.getId(),
        r.getReceiptNo(),
        r.getIssuedAt(),
        r.getMethod(),
        new ReceiptResponse.School("", ""),
        new ReceiptResponse.Student(student.getId(), student.getFullName(), null),
        new ReceiptResponse.Cashier(cashierUserId, null),
        outLines,
        r.getTotalAmount()
    );
  }

  @Transactional(readOnly = true)
  @Override
  public ReceiptResponse readReceipt(Long receiptId) {
    Receipt r = receiptRepo.findById(receiptId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Receipt not found"));

    var lines = r.getLines().stream().map(l ->
        new ReceiptResponse.Line(
            l.getGroup()==null? null : l.getGroup().getName(),
            l.getModel(), l.getPeriod(), l.getSessions(), l.getHours(), l.getAmount(), null
        )
    ).toList();

    Student st = r.getStudent();
    return new ReceiptResponse(
        r.getId(), r.getReceiptNo(), r.getIssuedAt(), r.getMethod(),
        new ReceiptResponse.School("", ""),
        new ReceiptResponse.Student(st.getId(), st.getFullName(), null),
        new ReceiptResponse.Cashier(r.getCashierUserId(), null),
        lines, r.getTotalAmount()
    );
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReceiptResponse> history(Long studentId) {
    return receiptRepo.findByStudent_IdOrderByIssuedAtDesc(studentId).stream().map(r -> {
      var lines = r.getLines().stream().map(l ->
          new ReceiptResponse.Line(
              l.getGroup()==null? null : l.getGroup().getName(),
              l.getModel(), l.getPeriod(), l.getSessions(), l.getHours(), l.getAmount(), null
          )
      ).toList();
      Student st = r.getStudent();
      return new ReceiptResponse(
          r.getId(), r.getReceiptNo(), r.getIssuedAt(), r.getMethod(),
          new ReceiptResponse.School("", ""),
          new ReceiptResponse.Student(st.getId(), st.getFullName(), null),
          new ReceiptResponse.Cashier(r.getCashierUserId(), null),
          lines, r.getTotalAmount()
      );
    }).toList();
  }
}
