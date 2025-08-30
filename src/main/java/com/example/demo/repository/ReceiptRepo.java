package com.example.demo.repository;

import com.example.demo.models.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptRepo extends JpaRepository<Receipt, Long> {
  long countByReceiptNoStartingWith(String prefix);
  List<Receipt> findByStudent_IdOrderByIssuedAtDesc(Long studentId);
}
