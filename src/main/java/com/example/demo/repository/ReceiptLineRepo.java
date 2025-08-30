package com.example.demo.repository;
import com.example.demo.models.ReceiptLine;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReceiptLineRepo extends JpaRepository<ReceiptLine, Long> {}
