package com.example.demo.services.Interface;

import com.example.demo.dto.response.AttendanceMatrixResponse;
import com.example.demo.dto.response.SessionResponse;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public interface AttendanceService {
  SessionResponse start(Long scheduleId, LocalDate date, ZoneOffset offset);

  // one scan API usable before/after start
  SessionResponse scan(Long scheduleId, LocalDate date, Long studentId, ZoneOffset offset);
  SessionResponse scanBulk(Long scheduleId, LocalDate date, List<Long> studentIds, ZoneOffset offset);

  // teacher/TA bulk (by session) — optional but kept
  SessionResponse bulkCheckIn(Long sessionId, List<Long> studentIds, ZoneOffset offset);

  SessionResponse close(Long sessionId, ZoneOffset offset);

  AttendanceMatrixResponse matrix(Long groupId, LocalDate start, LocalDate endExclusive);

  SessionResponse summary(Long sessionId);
}
