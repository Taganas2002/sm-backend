// src/main/java/com/example/demo/repository/StudentRepo.java
package com.example.demo.repository;

import com.example.demo.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, Long> {
  Optional<Student> findByCardUid(String cardUid);
  boolean existsByCardUid(String cardUid);
}
