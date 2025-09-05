package com.example.demo.controllers;

import com.example.demo.dto.request.CreateAccountRequest;
import com.example.demo.dto.request.SaveUserMenuPermissionsRequest;
import com.example.demo.dto.request.UpdateAccountRequest;
import com.example.demo.dto.response.AccountDetailDto;
import com.example.demo.dto.response.AccountSummaryDto;
import com.example.demo.dto.response.PermissionsSnapshotDto;
import com.example.demo.models.*;
import com.example.demo.repository.*;
import com.example.demo.services.Interface.AuthorityService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

// import your TenantContext (adjust the package if different)
import com.example.demo.security.TenantContext;

@RestController
@RequestMapping("/api/admin/accounts")
public class AccountsAdminController {

  private final UserRepository userRepo;
  private final RoleRepository roleRepo;
  private final PermissionRepository permRepo;
  private final UserPermissionOverrideRepository overrideRepo;
  private final SchoolMembershipRepository membershipRepo;
  private final AuthorityService authorityService;
  private final PasswordEncoder passwordEncoder;

  public AccountsAdminController(UserRepository userRepo,
                                 RoleRepository roleRepo,
                                 PermissionRepository permRepo,
                                 UserPermissionOverrideRepository overrideRepo,
                                 SchoolMembershipRepository membershipRepo,
                                 AuthorityService authorityService,
                                 PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.roleRepo = roleRepo;
    this.permRepo = permRepo;
    this.overrideRepo = overrideRepo;
    this.membershipRepo = membershipRepo;
    this.authorityService = authorityService;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:ACCOUNTS_READ')")
  public ResponseEntity<?> list(@RequestParam(required = false) String search,
                                @RequestParam(required = false) Long roleId,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size) {
    Long schoolId = TenantContext.requireSchoolId(); // already in your code
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 200), Sort.by(Sort.Direction.DESC, "id"));
    // Use your existing repository method here (unchanged)
    Page<User> p = userRepo.searchAccounts(search, roleId, pageable);
    var content = p.getContent().stream().map(this::toSummary).toList();
    return ResponseEntity.ok(Map.of(
        "content", content,
        "page", p.getNumber(),
        "size", p.getSize(),
        "total", p.getTotalElements()
    ));
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:ACCOUNTS_READ')")
  public ResponseEntity<?> get(@PathVariable Long userId) {
    return ResponseEntity.ok(toDetail(userRepo.findById(userId).orElseThrow()));
  }

  @GetMapping("/{userId}/permissions")
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:ACCOUNTS_READ')")
  public ResponseEntity<?> snapshot(@PathVariable Long userId,
                                    @RequestParam(defaultValue = "true") boolean includeBaseline) {
    var u = userRepo.findById(userId).orElseThrow();

    List<String> baseline = Collections.emptyList();
    if (includeBaseline) {
      Long schoolId = TenantContext.requireSchoolId();
      var membership = membershipRepo.findActiveByUserIdAndSchoolId(userId, schoolId).orElse(null);
      if (membership != null) {
        baseline = permRepo.findAllForRole(membership.getRole()).stream()
            .map(Permission::getCode).toList();
      } else {
        // fallback to global baseline
        baseline = permRepo.findAllForUserViaRoles(userId).stream()
            .map(Permission::getCode).toList();
      }
    }

    var overrides = overrideRepo.findByUserId(userId).stream()
        .map(o -> new PermissionsSnapshotDto.OverrideItem(o.getPermission().getCode(), o.getEffect().name()))
        .toList();

    var effective = new ArrayList<>(authorityService.computeEffectiveAuthorities(userId));

    var dto = new PermissionsSnapshotDto();
    dto.setUserId(userId);
    dto.setRoleId(u.getRoles().stream().findFirst().map(Role::getId).orElse(null));
    dto.setBaseline(baseline);
    dto.setOverrides(overrides);
    dto.setEffective(effective);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{userId}/permissions")
  @Transactional
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:USER_PERMS_WRITE')")
  public ResponseEntity<?> savePerms(@PathVariable Long userId,
                                     @RequestBody SaveUserMenuPermissionsRequest req) {
    var user = userRepo.findById(userId).orElseThrow();

    if (req.getRoleId() != null) {
      var role = roleRepo.findById(Long.valueOf(req.getRoleId())).orElseThrow();
      user.setRoles(Set.of(role));
      userRepo.save(user);
    }

    Long schoolId = TenantContext.requireSchoolId();
    var membership = membershipRepo.findActiveByUserIdAndSchoolId(user.getId(), schoolId).orElse(null);

    Set<String> baselineMenu;
    if (membership != null) {
      baselineMenu = permRepo.findAllForRole(membership.getRole()).stream()
          .map(Permission::getCode)
          .filter(c -> c.startsWith("MENU:"))
          .collect(Collectors.toCollection(LinkedHashSet::new));
    } else {
      baselineMenu = permRepo.findAllForUserViaRoles(user.getId()).stream()
          .map(Permission::getCode)
          .filter(c -> c.startsWith("MENU:"))
          .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    if (req.getMenuSelections() != null) {
      for (var sel : req.getMenuSelections()) {
        String code = sel.getCode();
        boolean want = sel.isChecked();
        Permission p = permRepo.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Unknown permission: " + code));
        boolean base = baselineMenu.contains(code);
        var existing = overrideRepo.findByUserIdAndPermissionId(user.getId(), p.getId());

        if (base && !want) {
          var ov = existing.orElseGet(UserPermissionOverride::new);
          ov.setUser(user);
          ov.setPermission(p);
          ov.setEffect(UserPermissionOverride.Effect.DENY);
          overrideRepo.save(ov);
        } else if (!base && want) {
          var ov = existing.orElseGet(UserPermissionOverride::new);
          ov.setUser(user);
          ov.setPermission(p);
          ov.setEffect(UserPermissionOverride.Effect.ALLOW);
          overrideRepo.save(ov);
        } else {
          existing.ifPresent(overrideRepo::delete);
        }
      }
    }

    var effective = authorityService.computeEffectiveAuthorities(user.getId());
    return ResponseEntity.ok(Map.of("userId", user.getId(), "authorities", effective));
  }

  @PostMapping
  @Transactional
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:ACCOUNTS_WRITE')")
  public ResponseEntity<?> create(@RequestBody CreateAccountRequest req) {
    if (req.getEmail() != null && userRepo.existsByEmail(req.getEmail()))
      return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
    if (req.getPhone() != null && userRepo.existsByPhone(req.getPhone()))
      return ResponseEntity.badRequest().body(Map.of("error", "Phone already in use"));

    User u = new User(req.getName(), req.getEmail(), req.getPhone(),
                      passwordEncoder.encode(req.getPassword()));
    if (req.getRoleId() != null) {
      var role = roleRepo.findById(Long.valueOf(req.getRoleId())).orElseThrow();
      u.setRoles(Set.of(role));
    }
    u = userRepo.save(u);

    Long schoolId = TenantContext.requireSchoolId();
    var membership = membershipRepo.findActiveByUserIdAndSchoolId(u.getId(), schoolId).orElse(null);

    Set<String> baselineMenu;
    if (membership != null) {
      baselineMenu = permRepo.findAllForRole(membership.getRole()).stream()
          .map(Permission::getCode)
          .filter(c -> c.startsWith("MENU:"))
          .collect(Collectors.toCollection(LinkedHashSet::new));
    } else {
      baselineMenu = permRepo.findAllForUserViaRoles(u.getId()).stream()
          .map(Permission::getCode)
          .filter(c -> c.startsWith("MENU:"))
          .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    if (req.getInitialMenuSelections() != null) {
      for (var sel : req.getInitialMenuSelections()) {
        String code = sel.getCode();
        boolean want = sel.isChecked();
        Permission p = permRepo.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Unknown permission: " + code));
        boolean base = baselineMenu.contains(code);
        if (base && !want) {
          var ov = new UserPermissionOverride();
          ov.setUser(u);
          ov.setPermission(p);
          ov.setEffect(UserPermissionOverride.Effect.DENY);
          overrideRepo.save(ov);
        } else if (!base && want) {
          var ov = new UserPermissionOverride();
          ov.setUser(u);
          ov.setPermission(p);
          ov.setEffect(UserPermissionOverride.Effect.ALLOW);
          overrideRepo.save(ov);
        }
      }
    }
    return ResponseEntity.ok(toDetail(u));
  }

  @PutMapping("/{userId}")
  @Transactional
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('API:ACCOUNTS_WRITE')")
  public ResponseEntity<?> update(@PathVariable Long userId, @RequestBody UpdateAccountRequest req) {
    var u = userRepo.findById(userId).orElseThrow();
    if (req.getName() != null && !req.getName().isBlank()) u.setUsername(req.getName());
    if (req.getEmail() != null && !req.getEmail().isBlank()) u.setEmail(req.getEmail());
    if (req.getPhone() != null && !req.getPhone().isBlank()) u.setPhone(req.getPhone());
    userRepo.save(u);
    return ResponseEntity.ok(toDetail(u));
  }

  private AccountSummaryDto toSummary(User u) {
    var roles = u.getRoles().stream().map(r -> r.getName().name()).toList();
    return new AccountSummaryDto(u.getId(), u.getUsername(), u.getEmail(), u.getPhone(), roles);
  }

  private AccountDetailDto toDetail(User u) {
    var roles = u.getRoles().stream().map(r -> r.getName().name()).toList();
    return new AccountDetailDto(u.getId(), u.getUsername(), u.getEmail(), u.getPhone(), roles);
  }
}
