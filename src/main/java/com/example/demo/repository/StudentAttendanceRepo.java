package com.example.demo.repository;

import com.example.demo.models.StudentAttendance;
import com.example.demo.models.enums.AttendanceMark;
import com.example.demo.models.enums.AttendanceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentAttendanceRepo extends JpaRepository<StudentAttendance, Long> {
  boolean existsBySessionIdAndStudentId(Long sessionId, Long studentId);
  long countBySessionId(Long sessionId);
  List<StudentAttendance> findBySessionId(Long sessionId);
  long countByStudentIdAndSessionGroupId(Long studentId, Long groupId);

  // Used by runningConsumption: counts all rows (present+absent) for (student, group)
  long countByStudentIdAndSession_Group_Id(Long studentId, Long groupId);

  Optional<StudentAttendance> findBySessionIdAndStudentId(Long sessionId, Long studentId);
  long countBySessionIdAndStatus(Long sessionId, AttendanceStatus status);
  // Present count (ignore ABSENT)
  long countBySessionIdAndStatus(Long sessionId, AttendanceMark status);


  @Query("select a.student.id from StudentAttendance a where a.session.id = :sessionId and a.status = 'PRESENT'")
  List<Long> findPresentStudentIdsBySessionId(@Param("sessionId") Long sessionId);
  
}
