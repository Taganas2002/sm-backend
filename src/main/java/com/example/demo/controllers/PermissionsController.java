package com.example.demo.controllers;

import com.example.demo.models.Permission;
import com.example.demo.repository.PermissionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {
  private final PermissionRepository perms;
  public PermissionsController(PermissionRepository perms) { this.perms = perms; }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:ACCOUNTS_READ')")
  public ResponseEntity<?> list(@RequestParam(required = false) String type) {
    var all = perms.findAll().stream().map(Permission::getCode);
    if (type == null || type.isBlank()) return ResponseEntity.ok(all.toList());
    String prefix = type.toUpperCase(Locale.ROOT) + ":";
    return ResponseEntity.ok(all.filter(c -> c.startsWith(prefix)).collect(Collectors.toList()));
  }
}
