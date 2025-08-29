package com.example.demo.repository;

import com.example.demo.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, Long> {
  Optional<Student> findByCardUid(String cardUid);
  
  @Query("""
		    select e.student.id
		    from Enrollment e
		    where e.group.id = :groupId
		      and e.status = com.example.demo.models.enums.EnrollmentStatus.ACTIVE
		  """)
		  List<Long> findActiveEnrolledStudentIds(@Param("groupId") Long groupId);
  
  @Query("""
	         select e.student.id
	         from Enrollment e
	         where e.group.id = :groupId
	           and e.status = com.example.demo.models.enums.EnrollmentStatus.ACTIVE
	         """)
	  List<Long> findEnrolledStudentIds(@Param("groupId") Long groupId);
}
