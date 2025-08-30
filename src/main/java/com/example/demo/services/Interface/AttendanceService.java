package com.example.demo.services.Interface;

import com.example.demo.dto.response.AttendanceMatrixResponse;
import com.example.demo.dto.response.SessionResponse;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public interface AttendanceService {

  // Start (idempotent) – schedule + date
  SessionResponse start(Long scheduleId, LocalDate date, ZoneOffset offset);

  // Scan (single) – works before/after start
  SessionResponse scan(Long scheduleId, LocalDate date, Long studentId, ZoneOffset offset);

  // Scan (bulk) – works before/after start
  SessionResponse scanBulk(Long scheduleId, LocalDate date, List<Long> studentIds, ZoneOffset offset);

  // Teacher bulk check-in by session (OPEN only)
  SessionResponse bulkCheckIn(Long sessionId, List<Long> studentIds, ZoneOffset offset);

  // NEW: toggle (checkbox) – present=true => PRESENT, false => ABSENT (OPEN only)
  SessionResponse mark(Long sessionId, Long studentId, boolean present, ZoneOffset offset);

  // Close (auto-ABSENT)
  SessionResponse close(Long sessionId, ZoneOffset offset);

  // Quick UI hydrate (who’s checked)
  SessionResponse summary(Long sessionId);

  // Matrix
  AttendanceMatrixResponse matrix(Long groupId, LocalDate start, LocalDate endExclusive);
}
