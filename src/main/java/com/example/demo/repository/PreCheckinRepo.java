// src/main/java/com/example/demo/repository/PreCheckinRepo.java
package com.example.demo.repository;

import com.example.demo.models.PreCheckin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PreCheckinRepo extends JpaRepository<PreCheckin, Long> {
  List<PreCheckin> findByGroup_IdAndSchedule_IdAndSessionDate(Long groupId, Long scheduleId, LocalDate date);
  void deleteByGroup_IdAndSchedule_IdAndSessionDate(Long groupId, Long scheduleId, LocalDate date);
}
