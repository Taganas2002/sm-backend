// src/main/java/com/example/demo/repository/TeacherEarningRepo.java
package com.example.demo.repository;

import com.example.demo.models.TeacherEarning;
import com.example.demo.models.enums.EarningStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface TeacherEarningRepo extends JpaRepository<TeacherEarning, Long> {

  @Query("""
    select e from TeacherEarning e
    where e.teacher.id = :tid
      and (:gid is null or e.group.id = :gid)
      and (:from is null or e.recognizedAt >= :from)
      and (:to   is null or e.recognizedAt <  :to)
      and (:status is null or e.status = :status)
    order by e.recognizedAt desc
  """)
  List<TeacherEarning> search(@Param("tid") Long teacherId,
                              @Param("gid") Long groupId,
                              @Param("from") OffsetDateTime from,
                              @Param("to") OffsetDateTime to,
                              @Param("status") EarningStatus status);

  @Query("""
    select coalesce(sum(e.shareAmount),0)
    from TeacherEarning e
    where e.teacher.id = :tid
      and (:gid is null or e.group.id = :gid)
      and (:from is null or e.recognizedAt >= :from)
      and (:to   is null or e.recognizedAt <  :to)
      and e.status = 'UNPAID'
  """)
  BigDecimal sumUnpaid(@Param("tid") Long teacherId,
                       @Param("gid") Long groupId,
                       @Param("from") OffsetDateTime from,
                       @Param("to") OffsetDateTime to);

  List<TeacherEarning> findByIdIn(List<Long> ids);

boolean existsByStudentPayment_Id(Long id);
}
