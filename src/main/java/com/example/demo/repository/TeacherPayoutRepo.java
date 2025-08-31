// src/main/java/com/example/demo/repository/TeacherPayoutRepo.java
package com.example.demo.repository;

import com.example.demo.models.TeacherPayout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherPayoutRepo extends JpaRepository<TeacherPayout, Long> {
  List<TeacherPayout> findByTeacher_IdOrderByIssuedAtDesc(Long teacherId);
  boolean existsByPayoutNo(String payoutNo);
}
