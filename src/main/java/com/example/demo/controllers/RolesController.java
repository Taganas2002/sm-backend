package com.example.demo.controllers;

import com.example.demo.repository.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RolesController {
  private final RoleRepository roles;
  public RolesController(RoleRepository roles) { this.roles = roles; }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:ACCOUNTS_READ')")
  public ResponseEntity<?> list() {
    return ResponseEntity.ok(roles.findAll().stream().map(r -> java.util.Map.of("id", r.getId(), "name", r.getName().name())).toList());
  }
}
