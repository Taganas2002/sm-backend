// com/example/demo/repository/AttendanceRepo.java
package com.example.demo.repository;

import com.example.demo.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
  Optional<Attendance> findBySession_IdAndStudent_Id(Long sessionId, Long studentId);
  long countBySession_Id(Long sessionId);
}
