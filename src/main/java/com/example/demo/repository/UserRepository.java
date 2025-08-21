package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ✅ Spring Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByPhone(String phoneNumber);
  Boolean existsByPhone(String phone);
  Boolean existsByEmail(String email);

  @Query("""
         select distinct u from User u
           left join u.roles r
          where (:search is null or :search = '' or
                 lower(u.username) like lower(concat('%', :search, '%')) or
                 lower(u.email)    like lower(concat('%', :search, '%')) or
                 u.phone           like concat('%', :search, '%'))
            and (:roleId is null or r.id = :roleId)
         """)
  Page<User> searchAccounts(@Param("search") String search,
                            @Param("roleId") Long roleId,   // ✅ Long
                            Pageable pageable);              // ✅ Spring Pageable
}
