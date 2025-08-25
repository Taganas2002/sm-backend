package com.example.demo.repository;

import com.example.demo.models.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepo extends JpaRepository<StudyGroup, Long> {
}
