package com.example.demo.controllers;

import com.example.demo.dto.request.EnrollmentCreateRequest;
import com.example.demo.dto.response.EnrollmentResponse;
import com.example.demo.dto.response.EnrollmentStatusSummaryResponse;
import com.example.demo.models.enums.EnrollmentStatus;
import com.example.demo.services.Interface.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

  private final EnrollmentService service;
  public EnrollmentController(EnrollmentService service){ this.service = service; }

  // create
  @PostMapping
  public EnrollmentResponse enroll(@RequestBody @Valid EnrollmentCreateRequest req){
    return service.enroll(req);
  }

  // list (existing: single status)
  @GetMapping
  public Page<EnrollmentResponse> list(@RequestParam(required=false) Long groupId,
                                       @RequestParam(required=false) Long studentId,
                                       @RequestParam(required=false) EnrollmentStatus status,
                                       Pageable pageable){
    return service.list(groupId, studentId, status, pageable);
  }

  // NEW: list with MULTIPLE statuses: use ?status=ACTIVE&status=SUSPENDED or ?statuses=ACTIVE,SUSPENDED
  @GetMapping("/filter")
  public Page<EnrollmentResponse> listByStatuses(@RequestParam(required=false) Long groupId,
                                                 @RequestParam(required=false) Long studentId,
                                                 @RequestParam(required=false, name = "status") List<EnrollmentStatus> statusList,
                                                 @RequestParam(required=false, name = "statuses") String statusesCsv,
                                                 Pageable pageable) {

    EnumSet<EnrollmentStatus> statuses = parseStatuses(statusList, statusesCsv);

    // If no statuses provided, default to all 4 main states (ACTIVE, SUSPENDED, DROPPED, COMPLETED)
    if (statuses.isEmpty()) {
      statuses = EnumSet.of(
          EnrollmentStatus.ACTIVE,
          EnrollmentStatus.SUSPENDED,
          EnrollmentStatus.DROPPED,
          EnrollmentStatus.COMPLETED
      );
    }

    return service.listByStatuses(groupId, studentId, statuses, pageable);
  }

  // detail
  @GetMapping("/{id}")
  public EnrollmentResponse get(@PathVariable Long id){
    return service.get(id);
  }

  // update status
  @PatchMapping("/{id}/status")
  public void updateStatus(@PathVariable Long id,
                           @RequestParam EnrollmentStatus status,
                           @RequestParam(required=false) String notes){
    service.updateStatus(id, status, notes);
  }

  // delete
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id){
    service.delete(id);
  }

  // NEW: per-group status summary (counts + capacity info)
  @GetMapping("/groups/{groupId}/status-summary")
  public EnrollmentStatusSummaryResponse summary(@PathVariable Long groupId) {
    return service.summaryByGroup(groupId);
  }

  // ---- helpers ----
  private EnumSet<EnrollmentStatus> parseStatuses(List<EnrollmentStatus> statusList, String csv) {
    EnumSet<EnrollmentStatus> set = EnumSet.noneOf(EnrollmentStatus.class);
    if (statusList != null) set.addAll(statusList);
    if (csv != null && !csv.isBlank()) {
      for (String token : csv.split(",")) {
        String trimmed = token.trim();
        if (!trimmed.isEmpty()) {
          set.add(EnrollmentStatus.valueOf(trimmed.toUpperCase()));
        }
      }
    }
    return set;
  }
}
