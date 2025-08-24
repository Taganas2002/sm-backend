package com.example.demo.repository;

import com.example.demo.models.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudyGroupRepo extends JpaRepository<StudyGroup, Long>, JpaSpecificationExecutor<StudyGroup> { }
