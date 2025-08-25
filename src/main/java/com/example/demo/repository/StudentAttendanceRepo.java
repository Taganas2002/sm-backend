package com.example.demo.repository;

import com.example.demo.models.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAttendanceRepo extends JpaRepository<StudentAttendance, Long> {
  boolean existsBySessionIdAndStudentId(Long sessionId, Long studentId);
  long countBySessionId(Long sessionId);
  List<StudentAttendance> findBySessionId(Long sessionId);
}
