package com.example.demo.repository;

import com.example.demo.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepo extends JpaRepository<Section, Long> {}
