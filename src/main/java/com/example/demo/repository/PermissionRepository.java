package com.example.demo.repository;

import com.example.demo.models.Permission;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
  Optional<Permission> findByCode(String code);

  @Query("""
    select distinct p from User u
    join u.roles r
    join r.permissions p
    where u.id = :userId
  """)
  List<Permission> findAllForUserViaRoles(Long userId);
}
