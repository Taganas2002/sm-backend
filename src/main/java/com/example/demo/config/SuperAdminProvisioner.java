package com.example.demo.config;

import com.example.demo.models.ERole;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.Interface.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * Production-safe bootstrap for a single Super Admin.
 *
 * Runs ONLY when app.superadmin.bootstrap.enabled=true.
 * After the first successful run, set the property to false and restart.
 */
@Component
@ConditionalOnProperty(prefix = "app.superadmin.bootstrap", name = "enabled", havingValue = "true")
public class SuperAdminProvisioner implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(SuperAdminProvisioner.class);

  private final UserRepository userRepo;
  private final RoleRepository roleRepo;
  private final PasswordEncoder encoder;
  private final EmailService emailService;

  @Value("${app.superadmin.bootstrap.username:Root}")
  private String username;

  @Value("${app.superadmin.bootstrap.email:root@yourdomain.com}")
  private String email;

  // Your auth uses phone for login; keep one canonical phone for SA
  @Value("${app.superadmin.bootstrap.phone:0555000000}")
  private String phone;

  /**
   * If "GENERATE", a strong password will be generated and emailed.
   * Otherwise this value is used as-is.
   */
  @Value("${app.superadmin.bootstrap.password:GENERATE}")
  private String bootstrapPassword;

  /**
   * Where to send the generated password. If blank, it is sent to the SA email.
   */
  @Value("${app.superadmin.bootstrap.notifyEmail:}")
  private String notifyEmail;

  public SuperAdminProvisioner(UserRepository userRepo,
                               RoleRepository roleRepo,
                               PasswordEncoder encoder,
                               EmailService emailService) {
    this.userRepo = userRepo;
    this.roleRepo = roleRepo;
    this.encoder = encoder;
    this.emailService = emailService;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    // 1) Ensure ROLE_SUPER_ADMIN exists
    Role saRole = roleRepo.findByName(ERole.ROLE_SUPER_ADMIN)
        .orElseGet(() -> roleRepo.save(new Role(ERole.ROLE_SUPER_ADMIN)));

    // 2) Fetch user (with roles) by phone/email
    Optional<User> byPhone = userRepo.findByPhoneWithRoles(phone);
    Optional<User> byEmail = userRepo.findByEmailWithRoles(email);

    User sa = byPhone.or(() -> byEmail).orElseGet(() -> {
      User u = new User(username, email, phone, ""); // set password below
      u.setEmailVerified(true);                       // can sign in immediately
      return userRepo.save(u);
    });

    // 3) Set/rotate password
    String plain = "GENERATE".equalsIgnoreCase(bootstrapPassword)
        ? generateStrongPassword()
        : bootstrapPassword;

    if (sa.getPassword() == null || sa.getPassword().isBlank()
        || !"GENERATE".equalsIgnoreCase(bootstrapPassword)) {
      sa.setPassword(encoder.encode(plain));
      userRepo.save(sa);

      // Do NOT log plain password; optionally email it to a secure inbox.
      String target = (notifyEmail == null || notifyEmail.isBlank()) ? email : notifyEmail;
      try {
        // Reuse existing mailer; the second parameter is just text for now.
        emailService.sendPasswordResetEmail(
            target,
            "Super Admin credentials\n\nPhone: " + phone +
                "\nEmail: " + email +
                "\nPassword: " + plain +
                "\n\nPlease sign in and rotate the password immediately."
        );
      } catch (Exception e) {
        log.error("Failed to email Super Admin bootstrap password. Rotate it manually.", e);
      }
    }

    // 4) Grant SA role if missing
    boolean hasRole = sa.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_SUPER_ADMIN);
    if (!hasRole) {
      sa.getRoles().add(saRole);
      userRepo.save(sa);
      log.info("Granted ROLE_SUPER_ADMIN to user id={} phone={}", sa.getId(), sa.getPhone());
    } else {
      log.info("Super Admin user id={} already has ROLE_SUPER_ADMIN", sa.getId());
    }

    log.warn("Super Admin bootstrap completed. DISABLE `app.superadmin.bootstrap.enabled` now.");
  }

  private static String generateStrongPassword() {
    byte[] buf = new byte[24]; // ~32 Base64 chars
    new SecureRandom().nextBytes(buf);
    return Base64.getEncoder().withoutPadding().encodeToString(buf);
  }
}
