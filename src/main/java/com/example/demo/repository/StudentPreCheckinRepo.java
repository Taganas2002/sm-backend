// src/main/java/com/example/demo/repository/StudentPreCheckinRepo.java
package com.example.demo.repository;

import com.example.demo.models.StudentPreCheckin;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPreCheckinRepo extends JpaRepository<StudentPreCheckin, Long> {
  List<StudentPreCheckin> findByGroup_IdAndSchedule_IdAndSessionDate(
      Long groupId, Long scheduleId, LocalDate sessionDate);

  Optional<StudentPreCheckin> findByGroup_IdAndSchedule_IdAndSessionDateAndStudent_Id(
      Long groupId, Long scheduleId, LocalDate sessionDate, Long studentId);

  void deleteByGroup_IdAndSchedule_IdAndSessionDate(
      Long groupId, Long scheduleId, LocalDate sessionDate);
}
