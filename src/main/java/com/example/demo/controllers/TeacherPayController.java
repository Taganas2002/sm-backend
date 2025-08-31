// src/main/java/com/example/demo/controllers/TeacherPayController.java
package com.example.demo.controllers;

import com.example.demo.dto.response.*;
import com.example.demo.services.Interface.TeacherPayService;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/billing/teacher")
public class TeacherPayController {

  private final TeacherPayService svc;
  public TeacherPayController(TeacherPayService svc){ this.svc = svc; }

  @GetMapping("/{teacherId}/summary")
  public TeacherSummaryResponse summary(
      @PathVariable Long teacherId,
      @RequestParam(required=false) Long groupId,
      @RequestParam(required=false) String from,
      @RequestParam(required=false) String to
  ) {
    return svc.summary(teacherId, groupId,
        from==null? null : OffsetDateTime.parse(from),
        to==null? null : OffsetDateTime.parse(to));
  }

  @GetMapping("/{teacherId}/earnings")
  public List<TeacherEarningRow> earnings(
      @PathVariable Long teacherId,
      @RequestParam(required=false, defaultValue="ALL") String status, // UNPAID|PAID|ALL
      @RequestParam(required=false) Long groupId,
      @RequestParam(required=false) String from,
      @RequestParam(required=false) String to
  ) {
    return svc.earnings(teacherId, status, groupId,
        from==null? null : OffsetDateTime.parse(from),
        to==null? null : OffsetDateTime.parse(to));
  }

  public static class CreatePayoutBody {
    public List<Long> earningIds;
    public String method;
    public String reference;
    public Long cashierUserId;
  }

  @PostMapping("/{teacherId}/payouts")
  public TeacherPayoutResponse createPayout(
      @PathVariable Long teacherId, @RequestBody CreatePayoutBody body) {
    return svc.createPayout(teacherId, body.earningIds, body.method, body.reference, body.cashierUserId);
  }

  @GetMapping("/payouts/{payoutId}")
  public TeacherPayoutResponse readPayout(@PathVariable Long payoutId) {
    return svc.readPayout(payoutId);
  }

  @GetMapping("/{teacherId}/payouts")
  public List<TeacherPayoutResponse> list(@PathVariable Long teacherId) {
    return svc.listPayouts(teacherId);
  }
//src/main/java/com/example/demo/controllers/TeacherPayController.java
@PostMapping("/{teacherId}/rebuild-earnings")
public TeacherSummaryResponse rebuild(@PathVariable Long teacherId,
                                     @RequestParam(required=false) String from,
                                     @RequestParam(required=false) String to) {
 svc.rebuildFromPayments(
     teacherId,
     from==null? null : OffsetDateTime.parse(from),
     to==null? null : OffsetDateTime.parse(to)
 );
 return svc.summary(teacherId, null, null, null);
}

}
