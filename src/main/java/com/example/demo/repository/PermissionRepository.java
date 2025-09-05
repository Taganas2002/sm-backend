package com.example.demo.repository;

import com.example.demo.models.ERole;
import com.example.demo.models.Permission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

  Optional<Permission> findByCode(String code);

  /** Baseline via a user's GLOBAL roles (old behavior, still used as fallback). */
  @Query("""
         select distinct p
         from User u
           join u.roles r
           join r.permissions p
         where u.id = :userId
         """)
  List<Permission> findAllForUserViaRoles(@Param("userId") Long userId);

  /** Baseline via a specific role (use this for the active school membership role). */
  @Query("""
         select distinct p
         from Role r
           join r.permissions p
         where r.name = :roleName
         """)
  List<Permission> findAllForRole(@Param("roleName") ERole roleName);
}
