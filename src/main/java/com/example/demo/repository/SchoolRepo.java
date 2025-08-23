package com.example.demo.repository;

import com.example.demo.models.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepo extends JpaRepository<School, Long> {}
