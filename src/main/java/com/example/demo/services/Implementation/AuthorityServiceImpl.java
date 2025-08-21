package com.example.demo.services.Implementation;

import com.example.demo.models.Permission;
import com.example.demo.models.UserPermissionOverride;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.UserPermissionOverrideRepository;
import com.example.demo.services.Interface.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorityServiceImpl implements AuthorityService {
  private final PermissionRepository permissionRepository;
  private final UserPermissionOverrideRepository overrideRepository;

  public AuthorityServiceImpl(PermissionRepository p, UserPermissionOverrideRepository o) {
    this.permissionRepository = p; this.overrideRepository = o;
  }

  @Override
  public Set<String> computeEffectiveAuthorities(Long userId) {
    Set<String> base = permissionRepository.findAllForUserViaRoles(userId).stream()
        .map(Permission::getCode).collect(Collectors.toSet());

    var overrides = overrideRepository.findByUserId(userId);
    var allow = overrides.stream()
        .filter(o -> o.getEffect() == UserPermissionOverride.Effect.ALLOW)
        .map(o -> o.getPermission().getCode()).collect(Collectors.toSet());
    var deny = overrides.stream()
        .filter(o -> o.getEffect() == UserPermissionOverride.Effect.DENY)
        .map(o -> o.getPermission().getCode()).collect(Collectors.toSet());

    base.addAll(allow);
    base.removeAll(deny);
    return base;
  }
}
