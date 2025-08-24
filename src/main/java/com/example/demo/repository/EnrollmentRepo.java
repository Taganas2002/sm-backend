package com.example.demo.repository;

import com.example.demo.models.Enrollment;
import com.example.demo.models.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EnrollmentRepo extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
  long countByGroupIdAndStatus(Long groupId, EnrollmentStatus status);
  boolean existsByStudentIdAndGroupIdAndStatusIn(Long studentId, Long groupId,
      java.util.Collection<EnrollmentStatus> statuses);
}
