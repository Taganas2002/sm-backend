package com.example.demo.services.Interface;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AttendanceService {

  // A) Teacher starts a session (QR or manual), slot-identified (date/start/end)
  SessionSummaryResponse teacherStart(TeacherStartRequest req);

  // B) Student present (QR or manual), slot-identified OR by sessionId
  StudentPresentResponse studentPresent(StudentPresentRequest req);
  StudentPresentResponse studentPresentBySessionId(Long sessionId, Long studentId, String studentToken, String source);

  // C) Bulk present (manual) for a known slot OR by sessionId
  BulkPresentResponse bulkPresent(BulkPresentRequest req);
  BulkPresentResponse bulkPresentBySessionId(Long sessionId, List<Long> studentIds, String source);

  // D) Close session (auto-absent happens inside impl)
  CloseSessionResponse closeSession(Long sessionId, String reason);

  // E) Live session
  LiveSessionResponse liveSession(Long groupId);

  // F) Sessions list & detail
  SessionsListResponse listSessions(Long groupId, LocalDate from, LocalDate to, boolean includePlanned);
  SessionDetailResponse getSession(Long sessionId);

  // G) Running consumption (X / quota)
  ConsumptionRunningResponse runningConsumption(Long studentId, Long groupId);

  // Utility (optional) to resolve/create session id from slot identity (for UI flows)
  Long ensureSessionId(Long groupId, LocalDate slotDate, LocalTime startTime, LocalTime endTime, boolean openIfTeacherStart);

  // Roster grid: set single cell (Present/Absent)
  SessionDetailResponse setRosterMark(Long sessionId, Long studentId, String status, String source);
}
