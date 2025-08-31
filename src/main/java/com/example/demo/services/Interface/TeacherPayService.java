// src/main/java/com/example/demo/services/Interface/TeacherPayService.java
package com.example.demo.services.Interface;

import com.example.demo.dto.response.*;
import com.example.demo.models.StudentPayment;

import java.time.OffsetDateTime;
import java.util.List;

public interface TeacherPayService {
  void accrueOnStudentPayment(StudentPayment sp);

  TeacherSummaryResponse summary(Long teacherId,
                                 Long groupId,
                                 OffsetDateTime from,
                                 OffsetDateTime to);

  List<TeacherEarningRow> earnings(Long teacherId,
                                   String status,   // UNPAID | PAID | ALL
                                   Long groupId,
                                   OffsetDateTime from,
                                   OffsetDateTime to);

  TeacherPayoutResponse createPayout(Long teacherId,
                                     List<Long> earningIds,
                                     String method,
                                     String reference,
                                     Long cashierUserId);

  TeacherPayoutResponse readPayout(Long payoutId);

  List<TeacherPayoutResponse> listPayouts(Long teacherId);
//in TeacherPayService
int rebuildFromPayments(Long teacherId, OffsetDateTime from, OffsetDateTime to);

}
