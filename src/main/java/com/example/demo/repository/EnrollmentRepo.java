package com.example.demo.repository;

import com.example.demo.models.Enrollment;
import com.example.demo.models.Student;
import com.example.demo.models.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- add this import
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface EnrollmentRepo
    extends JpaRepository<Enrollment, Long>,
            JpaSpecificationExecutor<Enrollment> { // <-- add this

  @Query("""
     select e.student
     from Enrollment e
     where e.group.id = :groupId
       and e.status = com.example.demo.models.enums.EnrollmentStatus.ACTIVE
       and e.enrollmentDate <= :onDate
  """)
  List<Student> findActiveStudentsOnDate(@Param("groupId") Long groupId,
                                         @Param("onDate") LocalDate onDate);

  @Query("""
     select count(e)
     from Enrollment e
     where e.group.id = :groupId
       and e.student.id = :studentId
       and e.status = com.example.demo.models.enums.EnrollmentStatus.ACTIVE
       and e.enrollmentDate <= :onDate
  """)
  long countActiveOnDate(@Param("studentId") Long studentId,
                         @Param("groupId") Long groupId,
                         @Param("onDate") LocalDate onDate);

  long countByGroupIdAndStatus(Long groupId, EnrollmentStatus status);

  boolean existsByStudentIdAndGroupIdAndStatusIn(Long studentId, Long groupId,
                                                 Collection<EnrollmentStatus> statuses);

  List<Enrollment> findByGroupIdAndStatusIn(Long groupId, Collection<EnrollmentStatus> statuses);

  List<Enrollment> findByStudentIdAndGroupId(Long studentId, Long groupId);
}
