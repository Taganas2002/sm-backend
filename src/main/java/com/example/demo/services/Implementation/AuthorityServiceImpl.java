package com.example.demo.services.Implementation;

import com.example.demo.models.Permission;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.SchoolMembershipRepository;
import com.example.demo.repository.UserPermissionOverrideRepository;
import com.example.demo.services.Interface.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorityServiceImpl implements AuthorityService {
  private final PermissionRepository permissionRepository;
  private final UserPermissionOverrideRepository overrideRepository;
  private final SchoolMembershipRepository membershipRepo;

  public AuthorityServiceImpl(PermissionRepository p,
                              UserPermissionOverrideRepository o,
                              SchoolMembershipRepository m) {
    this.permissionRepository = p;
    this.overrideRepository = o;
    this.membershipRepo = m;
  }

  /** Keep existing signature used by your code. Baseline = global roles + overrides. */
  @Override
  public Set<String> computeEffectiveAuthorities(Long userId) {
    Set<String> base = permissionRepository.findAllForUserViaRoles(userId).stream()
        .map(Permission::getCode).collect(Collectors.toSet());

    var overrides = overrideRepository.findByUserId(userId);
    var allow = overrides.stream()
        .filter(o -> o.getEffect() == com.example.demo.models.UserPermissionOverride.Effect.ALLOW)
        .map(o -> o.getPermission().getCode()).collect(Collectors.toSet());
    var deny = overrides.stream()
        .filter(o -> o.getEffect() == com.example.demo.models.UserPermissionOverride.Effect.DENY)
        .map(o -> o.getPermission().getCode()).collect(Collectors.toSet());

    base.addAll(allow);
    base.removeAll(deny);
    return base;
  }

  /** Optional helper if you want tenant-scoped authorities later. */
  public Set<String> computeEffectiveAuthorities(Long userId, Long schoolId) {
    var membership = membershipRepo.findActiveByUserIdAndSchoolId(userId, schoolId).orElse(null);
    Set<String> base;
    if (membership != null) {
      base = permissionRepository.findAllForRole(membership.getRole()).stream()
          .map(Permission::getCode).collect(Collectors.toSet());
    } else {
      base = permissionRepository.findAllForUserViaRoles(userId).stream()
          .map(Permission::getCode).collect(Collectors.toSet());
    }
    var overrides = overrideRepository.findByUserId(userId);
    var allow = overrides.stream()
        .filter(o -> o.getEffect() == com.example.demo.models.UserPermissionOverride.Effect.ALLOW)
        .map(o -> o.getPermission().getCode()).collect(Collectors.toSet());
    var deny = overrides.stream()
        .filter(o -> o.getEffect() == com.example.demo.models.UserPermissionOverride.Effect.DENY)
        .map(o -> o.getPermission().getCode()).collect(Collectors.toSet());
    base.addAll(allow);
    base.removeAll(deny);
    return base;
  }
}
