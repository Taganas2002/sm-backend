package com.example.demo.services.Interface;

import com.example.demo.dto.request.EnrollmentCreateRequest;
import com.example.demo.dto.response.EnrollmentResponse;
import com.example.demo.dto.response.EnrollmentStatusSummaryResponse;
import com.example.demo.models.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.EnumSet;

public interface EnrollmentService {
  EnrollmentResponse enroll(EnrollmentCreateRequest req);

  EnrollmentResponse get(Long id);

  // existing single-status listing
  Page<EnrollmentResponse> list(Long groupId, Long studentId, EnrollmentStatus status, Pageable pageable);

  // NEW: multi-status listing
  Page<EnrollmentResponse> listByStatuses(Long groupId, Long studentId, EnumSet<EnrollmentStatus> statuses, Pageable pageable);

  // NEW: summary for a group (counts per status + capacity)
  EnrollmentStatusSummaryResponse summaryByGroup(Long groupId);

  void updateStatus(Long id, EnrollmentStatus status, String notes);

  void delete(Long id);
}
