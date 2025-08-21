package com.example.demo.repository;

import com.example.demo.models.UserPermissionOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserPermissionOverrideRepository extends JpaRepository<UserPermissionOverride, Long> {
  List<UserPermissionOverride> findByUserId(Long userId);
  Optional<UserPermissionOverride> findByUserIdAndPermissionId(Long userId, Long permissionId);
}
