package com.example.demo.services.Interface;

import com.example.demo.dto.request.EnrollmentCreateRequest;
import com.example.demo.dto.response.EnrollmentResponse;
import com.example.demo.models.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {
  EnrollmentResponse enroll(EnrollmentCreateRequest req);
  EnrollmentResponse get(Long id);
  Page<EnrollmentResponse> list(Long groupId, Long studentId, EnrollmentStatus status, Pageable pageable);
  void updateStatus(Long id, EnrollmentStatus status, String notes);
  void delete(Long id);
}

