package com.example.demo.repository;

import com.example.demo.models.AttendanceSession;
import com.example.demo.models.StudyGroup;
import com.example.demo.models.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceSessionRepo extends JpaRepository<AttendanceSession, Long> {
  Optional<AttendanceSession> findByGroupAndSessionDateAndStartTimeAndEndTime(
      StudyGroup group, LocalDate date, LocalTime start, LocalTime end);

  List<AttendanceSession> findByStatusIn(List<SessionStatus> statuses);
}
