package com.example.demo.repository;

import com.example.demo.models.SchoolMembership;
import com.example.demo.models.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SchoolMembershipRepository extends JpaRepository<SchoolMembership, Long> {
  Optional<SchoolMembership> findByUserIdAndSchoolId(Long userId, Long schoolId);
  List<SchoolMembership> findByUserId(Long userId);
  boolean existsByUserIdAndSchoolId(Long userId, Long schoolId);
  long countBySchoolIdAndRole(Long schoolId, ERole role);
  Optional<SchoolMembership> findFirstByUserIdOrderByIdAsc(Long userId);
  @Query("""
	         select sm
	         from SchoolMembership sm
	         where sm.user.id = :userId and sm.school.id = :schoolId and sm.active = true
	         """)
	  Optional<SchoolMembership> findActiveByUserIdAndSchoolId(@Param("userId") Long userId,
	                                                            @Param("schoolId") Long schoolId);

  boolean existsByUserIdAndSchoolIdAndActiveTrue(Long userId, Long schoolId);

  

  Optional<SchoolMembership> findByUserIdAndSchoolIdAndActiveTrue(Long userId, Long schoolId);

  @Query("""
         select sm
         from SchoolMembership sm
           join fetch sm.school s
         where sm.user.id = :userId
           and sm.active = true
         order by s.id desc
         """)

  List<SchoolMembership> findByUserIdAndActiveTrue(Long userId);

  @Query("""
         select sm
         from SchoolMembership sm
         join fetch sm.school s
         where sm.user.id = :userId and sm.active = true
         """)
  List<SchoolMembership> findActiveWithSchoolByUserId(Long userId);

  /** true if the user is an active ADMIN of any school (i.e., owner/creator). */
  boolean existsByUserIdAndRoleAndActiveTrue(Long userId, ERole role);

  /** convenience: first active ADMIN membership of a school */
  Optional<SchoolMembership> findFirstBySchoolIdAndRoleAndActiveTrueOrderByIdAsc(Long schoolId, ERole role);
}
