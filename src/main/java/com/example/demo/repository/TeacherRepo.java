package com.example.demo.repository;

import com.example.demo.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepo extends JpaRepository<Teacher, Long> {
  Optional<Teacher> findByCardUid(String cardUid);
  Optional<Teacher> findByQrCode(String qrCode);
}
