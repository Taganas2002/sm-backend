package com.example.demo.repository;

import com.example.demo.models.StudentAttendance;
import com.example.demo.models.enums.StudentAttendanceStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface StudentAttendanceRepo extends JpaRepository<StudentAttendance, Long> {
  Optional<StudentAttendance> findBySession_IdAndStudent_Id(Long sessionId, Long studentId);
  List<StudentAttendance> findBySession_Id(Long sessionId);
  List<StudentAttendance> findBySession_IdIn(Collection<Long> sessionIds);
  long countBySession_IdAndStatus(Long sessionId, StudentAttendanceStatus status);

  @Query("select a.student.id from StudentAttendance a where a.session.id = :sessionId and a.status = :status")
  List<Long> findStudentIdsBySessionAndStatus(@Param("sessionId") Long sessionId,
                                              @Param("status") StudentAttendanceStatus status);
}
