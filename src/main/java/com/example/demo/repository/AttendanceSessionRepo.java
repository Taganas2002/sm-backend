// src/main/java/com/example/demo/repository/AttendanceSessionRepo.java
package com.example.demo.repository;

import com.example.demo.models.AttendanceSession;
import com.example.demo.models.GroupSchedule;
import com.example.demo.models.StudyGroup;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDate;
import java.util.*;

public interface AttendanceSessionRepo extends JpaRepository<AttendanceSession, Long> {
  Optional<AttendanceSession> findByGroupAndScheduleAndSessionDate(StudyGroup group, GroupSchedule schedule, LocalDate sessionDate);

  List<AttendanceSession> findByGroup_IdAndSessionDateBetweenOrderBySessionDateAsc(
      Long groupId, LocalDate start, LocalDate endExclusive);
}
