package com.example.demo.security;

import com.example.demo.models.ERole;
import com.example.demo.models.Permission;
import com.example.demo.models.Role;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class SeedPermissions {

  private final TransactionTemplate tx;

  public SeedPermissions(PlatformTransactionManager txManager) {
    this.tx = new TransactionTemplate(txManager);
  }

  @Bean
  CommandLineRunner seed(PermissionRepository perms, RoleRepository roles) {
    return args -> tx.executeWithoutResult(status -> {
      // 1) Ensure permission codes exist
      String[] MENU = {
          "MENU:HOME_VIEW","MENU:ATTENDANCE_VIEW","MENU:TEACHERS_VIEW","MENU:STUDENTS_VIEW",
          "MENU:GROUPS_VIEW","MENU:CLASSES_VIEW","MENU:SUBJECTS_VIEW","MENU:TIMETABLE_VIEW",
          "MENU:SPECIALTIES_VIEW","MENU:FINANCE_VIEW","MENU:REPORTS_VIEW","MENU:ABOUT_VIEW"
      };
      String[] API = {
          "API:STUDENTS_READ","API:STUDENTS_WRITE",
          "API:TEACHERS_READ","API:TEACHERS_WRITE",
          "API:CLASSES_READ","API:CLASSES_WRITE",
          "API:ATTENDANCE_READ","API:ATTENDANCE_WRITE",
          "API:FINANCE_READ","API:FINANCE_WRITE",
          "API:REPORTS_READ",
          "API:ACCOUNTS_READ","API:ACCOUNTS_WRITE","API:USER_PERMS_WRITE"
      };
      for (String c : MENU) perms.findByCode(c).orElseGet(() -> perms.save(new Permission(null, c, c)));
      for (String c : API)  perms.findByCode(c).orElseGet(() -> perms.save(new Permission(null, c, c)));

      // map code -> Permission
      Map<String, Permission> P = perms.findAll().stream()
          .collect(Collectors.toMap(Permission::getCode, p -> p));

      // 2) Ensure roles exist
      Role ADMIN      = ensureRole(roles, ERole.ROLE_ADMIN);
      Role TEACHER    = ensureRole(roles, ERole.ROLE_TEACHER);
      Role REGISTRAR  = ensureRole(roles, ERole.ROLE_REGISTRAR);
      Role ACCOUNTANT = ensureRole(roles, ERole.ROLE_ACCOUNTANT);
      Role PRINCIPAL  = ensureRole(roles, ERole.ROLE_PRINCIPAL);
      ensureRole(roles, ERole.ROLE_USER);
      ensureRole(roles, ERole.ROLE_MODERATOR);

      // 3) Baselines (replace the set via setter)
      setPerms(ADMIN, P, P.keySet());

      setPerms(TEACHER, P, List.of(
          "MENU:HOME_VIEW","MENU:ATTENDANCE_VIEW","MENU:CLASSES_VIEW","MENU:SUBJECTS_VIEW",
          "MENU:TIMETABLE_VIEW","MENU:REPORTS_VIEW",
          "API:ATTENDANCE_READ","API:ATTENDANCE_WRITE",
          "API:STUDENTS_READ","API:CLASSES_READ","API:REPORTS_READ"
      ));

      setPerms(REGISTRAR, P, List.of(
          "MENU:HOME_VIEW","MENU:STUDENTS_VIEW","MENU:CLASSES_VIEW","MENU:SUBJECTS_VIEW",
          "MENU:TIMETABLE_VIEW","MENU:GROUPS_VIEW","MENU:REPORTS_VIEW",
          "API:STUDENTS_READ","API:STUDENTS_WRITE",
          "API:CLASSES_READ","API:CLASSES_WRITE",
          "API:TEACHERS_READ",
          "API:REPORTS_READ"
      ));

      setPerms(ACCOUNTANT, P, List.of(
          "MENU:HOME_VIEW","MENU:FINANCE_VIEW","MENU:REPORTS_VIEW",
          "API:FINANCE_READ","API:FINANCE_WRITE","API:REPORTS_READ"
      ));

      setPerms(PRINCIPAL, P, List.of(
          "MENU:HOME_VIEW","MENU:ATTENDANCE_VIEW","MENU:TEACHERS_VIEW","MENU:STUDENTS_VIEW",
          "MENU:CLASSES_VIEW","MENU:SUBJECTS_VIEW","MENU:TIMETABLE_VIEW",
          "MENU:FINANCE_VIEW","MENU:REPORTS_VIEW",
          "API:STUDENTS_READ","API:TEACHERS_READ","API:CLASSES_READ",
          "API:ATTENDANCE_READ","API:FINANCE_READ","API:REPORTS_READ"
      ));

      roles.saveAll(List.of(ADMIN, TEACHER, REGISTRAR, ACCOUNTANT, PRINCIPAL));
    });
  }

  private Role ensureRole(RoleRepository roles, ERole name) {
    return roles.findByName(name).orElseGet(() -> roles.save(new Role(name)));
  }

  private void setPerms(Role role, Map<String, Permission> all, Collection<String> codes) {
    Set<Permission> newSet = codes.stream()
        .map(all::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    role.setPermissions(newSet); // safe replace; no lazy init required
  }
}
