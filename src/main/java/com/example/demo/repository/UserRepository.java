package com.example.demo.repository;

import com.example.demo.models.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByPhone(String phone);
  Optional<User> findByEmail(String email);
  Boolean existsByEmail(String email);
  Boolean existsByPhone(String phone);

  @Query("""
		  SELECT DISTINCT u
		  FROM User u
		  LEFT JOIN u.roles r
		  WHERE (:search IS NULL OR :search = '' OR
		         LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
		         LOWER(u.email)    LIKE LOWER(CONCAT('%', :search, '%')) OR
		         u.phone           LIKE CONCAT('%', :search, '%'))
		    AND (:roleId IS NULL OR r.id = :roleId)
		  """)
		  org.springframework.data.domain.Page<User> searchAccounts(
		      @org.springframework.data.repository.query.Param("search") String search,
		      @org.springframework.data.repository.query.Param("roleId") Long roleId,
		      org.springframework.data.domain.Pageable pageable);
  

  // ---- Fetch-join variants used by SuperAdminProvisioner ----
  @Query("""
         select u from User u
         left join fetch u.roles
         where u.phone = :phone
         """)
  Optional<User> findByPhoneWithRoles(@Param("phone") String phone);

  @Query("""
         select u from User u
         left join fetch u.roles
         where u.email = :email
         """)
  Optional<User> findByEmailWithRoles(@Param("email") String email);
}
