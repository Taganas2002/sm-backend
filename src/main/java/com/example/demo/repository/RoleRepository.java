package com.example.demo.repository;

import com.example.demo.models.ERole;
import com.example.demo.models.Role;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(ERole name);

  @Query("select r from Role r left join fetch r.permissions where r.name = :name")
  Optional<Role> findByNameWithPermissions(@Param("name") ERole name);

}
