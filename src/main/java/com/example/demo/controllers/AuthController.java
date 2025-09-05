package com.example.demo.controllers;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.MessageRequestResponse;
import com.example.demo.models.*;
import com.example.demo.models.enums.SubscriptionStatus;
import com.example.demo.models.enums.TokenStatus;
import com.example.demo.models.enums.TokenType;
import com.example.demo.repository.*;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;
import com.example.demo.services.Interface.AuthorityService;
import com.example.demo.services.Interface.EmailService;
import com.example.demo.util.Tokens;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Auth + account-level subscription gating.
 * - AccountSubscription is the paywall (TRIAL/PAID/EXPIRED).
 * - SUPER_ADMIN bypasses the paywall.
 * - If an account has no AccountSubscription row (legacy users),
 *   we automatically create a 15-day TRIAL on first sign in/verify.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final SchoolRepository schoolRepository;
  private final SchoolMembershipRepository membershipRepository;
  private final AccountSubscriptionRepository accountSubscriptionRepository;
  private final AuthTokenRepository tokenRepository;
  private final PasswordEncoder encoder;
  private final JwtUtils jwtUtils;
  private final AuthorityService authorityService;
  private final EmailService emailService;

  @Value("${app.frontend.base-url}") private String frontendBase;

  public AuthController(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      RoleRepository roleRepository,
      SchoolRepository schoolRepository,
      SchoolMembershipRepository membershipRepository,
      AccountSubscriptionRepository accountSubscriptionRepository,
      AuthTokenRepository tokenRepository,
      PasswordEncoder encoder,
      JwtUtils jwtUtils,
      AuthorityService authorityService,
      EmailService emailService
  ) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.schoolRepository = schoolRepository;
    this.membershipRepository = membershipRepository;
    this.accountSubscriptionRepository = accountSubscriptionRepository;
    this.tokenRepository = tokenRepository;
    this.encoder = encoder;
    this.jwtUtils = jwtUtils;
    this.authorityService = authorityService;
    this.emailService = emailService;
  }

  // ------------------------ SIGN IN ------------------------
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    var userOpt = userRepository.findByPhone(loginRequest.getPhone());
    if (userOpt.isEmpty())
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Invalid phone or password"));
    var user = userOpt.get();

    if (!user.isEmailVerified()) {
      return ResponseEntity.status(403).body(new MessageRequestResponse("Please verify your email to sign in."));
    }

    // SUPER_ADMIN bypasses, others must have an active account subscription
    if (!userHasRole(user, ERole.ROLE_SUPER_ADMIN) && !ensureAccountSubscriptionActive(user)) {
      return ResponseEntity.status(402).body(new MessageRequestResponse(
          "Your trial or paid plan has ended. Please upgrade to continue."));
    }

    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    var principal = (UserDetailsImpl) auth.getPrincipal();

    Long activeSchoolId = membershipRepository.findFirstByUserIdOrderByIdAsc(user.getId())
        .map(m -> m.getSchool().getId()).orElse(null);

    var effective = activeSchoolId != null
        ? authorityService.computeEffectiveAuthorities(user.getId(), activeSchoolId)
        : java.util.Set.<String>of();

    String jwt = jwtUtils.generateJwtToken(auth, effective, activeSchoolId);
    List<String> roles = principal.getAuthorities().stream().map(a -> a.getAuthority()).toList();

    return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getUsername(),
        user.getEmail(), user.getPhone(), roles, new ArrayList<>(effective)));
  }

  // -------- REGISTER SCHOOL (create admin + first school + 15-day ACCOUNT trial) --------
  @PostMapping("/register-school")
  @Transactional
  public ResponseEntity<?> registerSchool(@RequestBody RegisterSchoolRequest req) {
    if (userRepository.existsByEmail(req.getEmail()))
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Error: Email already in use!"));
    if (userRepository.existsByPhone(req.getPhone()))
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Error: Phone already in use!"));

    // user with baseline ROLE_ADMIN
    User user = new User(req.getUsername(), req.getEmail(), req.getPhone(), encoder.encode(req.getPassword()));
    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
    user.setRoles(Set.of(adminRole));
    user.setEmailVerified(false);
    user = userRepository.save(user);

    // first school
    School school = new School();
    school.setName(Optional.ofNullable(req.getSchoolName()).orElse("My School"));
    school.setEmail(req.getSchoolEmail());
    school.setPhone(req.getSchoolPhone());
    school.setAddress(req.getSchoolAddress());
    school.setActive(true);
    school = schoolRepository.save(school);

    // membership (ADMIN)
    SchoolMembership sm = new SchoolMembership();
    sm.setUser(user);
    sm.setSchool(school);
    sm.setRole(ERole.ROLE_ADMIN);
    sm.setActive(true);
    membershipRepository.save(sm);

    // ACCOUNT trial (15 days)
    var acc = new AccountSubscription();
    acc.setUser(user);
    acc.setStatus(SubscriptionStatus.TRIAL);
    acc.setTrialStart(LocalDateTime.now());
    acc.setTrialEnd(LocalDateTime.now().plusDays(15));
    accountSubscriptionRepository.save(acc);

    // email verify token (48h)
    String token = Tokens.randomUrlToken(48);
    String tokenHash = Tokens.sha256Hex(token);

    var t = new AuthToken();
    t.setUser(user);
    t.setSchool(school); // context only
    t.setTokenType(TokenType.EMAIL_VERIFY);
    t.setTokenHash(tokenHash);
    t.setStatus(TokenStatus.PENDING);
    t.setExpiresAt(LocalDateTime.now().plusDays(2));
    tokenRepository.save(t);

    String verifyUrl = frontendBase + "/verify-email?token=" + token;
    emailService.sendVerificationEmail(user.getEmail(), school.getName(), verifyUrl);

    return ResponseEntity.ok(new MessageRequestResponse(
        "Account created. We sent a verification email to " + user.getEmail() + ". Please verify to start."));
  }

  // -------- RESEND VERIFICATION --------
  @PostMapping("/resend-verification")
  @Transactional
  public ResponseEntity<?> resendVerification(@RequestBody ResendVerificationRequest req) {
    var user = userRepository.findByEmail(req.getEmail()).orElse(null);
    if (user == null) return ResponseEntity.ok(new MessageRequestResponse("If this email exists, we sent a new link."));
    if (user.isEmailVerified()) return ResponseEntity.ok(new MessageRequestResponse("Email already verified."));

    var membership = membershipRepository.findFirstByUserIdOrderByIdAsc(user.getId());
    var schoolName = membership.map(m -> m.getSchool().getName()).orElse("your account");

    String token = Tokens.randomUrlToken(48);
    String tokenHash = Tokens.sha256Hex(token);

    var t = new AuthToken();
    t.setUser(user);
    t.setSchool(membership.map(SchoolMembership::getSchool).orElse(null));
    t.setTokenType(TokenType.EMAIL_VERIFY);
    t.setTokenHash(tokenHash);
    t.setStatus(TokenStatus.PENDING);
    t.setExpiresAt(LocalDateTime.now().plusDays(2));
    tokenRepository.save(t);

    String verifyUrl = frontendBase + "/verify-email?token=" + token;
    emailService.sendVerificationEmail(user.getEmail(), schoolName, verifyUrl);
    return ResponseEntity.ok(new MessageRequestResponse("Verification email sent."));
  }

  // -------- VERIFY EMAIL (returns JWT if account subscription is active) --------
  @PostMapping("/verify-email")
  @Transactional
  public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest req) {
    String tokenHash = Tokens.sha256Hex(req.getToken());
    var t = tokenRepository.findByTokenHashAndTokenTypeAndStatus(
            tokenHash, TokenType.EMAIL_VERIFY, TokenStatus.PENDING)
        .orElseThrow(() -> new RuntimeException("Invalid or already used verification token"));

    if (t.getExpiresAt().isBefore(LocalDateTime.now()))
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Verification link expired. Please resend."));

    var user = t.getUser();
    user.setEmailVerified(true);
    userRepository.save(user);

    t.setStatus(TokenStatus.USED);
    t.setUsedAt(LocalDateTime.now());
    tokenRepository.save(t);

    // SUPER_ADMIN bypasses, others must be active (also auto-create trial if missing)
    if (!userHasRole(user, ERole.ROLE_SUPER_ADMIN) && !ensureAccountSubscriptionActive(user)) {
      return ResponseEntity.status(402).body(new MessageRequestResponse(
          "Your trial or paid plan has ended. Please upgrade to continue."));
    }

    Long schoolId = membershipRepository.findFirstByUserIdOrderByIdAsc(user.getId())
        .map(m -> m.getSchool().getId()).orElse(null);

    var effective = schoolId != null
        ? authorityService.computeEffectiveAuthorities(user.getId(), schoolId)
        : java.util.Set.<String>of();

    String jwt = jwtUtils.generateJwtTokenForPhone(user.getPhone(), effective, schoolId);
    List<String> roles = user.getRoles().stream().map(r -> r.getName().name()).toList();

    return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getUsername(),
        user.getEmail(), user.getPhone(), roles, new ArrayList<>(effective)));
  }

  // -------- CREATE ANOTHER SCHOOL (branch) --------
  @PostMapping("/create-school")
  @Transactional
  public ResponseEntity<?> createAnotherSchool(@RequestBody CreateSchoolRequest req) {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = principal.getId();

    if (!ensureAccountSubscriptionActive(userRepository.getReferenceById(userId))
        && !userHasRole(userRepository.getReferenceById(userId), ERole.ROLE_SUPER_ADMIN)) {
      return ResponseEntity.status(402).body(new MessageRequestResponse(
          "Your trial or paid plan has ended. Please upgrade to continue."));
    }

    School school = new School();
    school.setName(req.getSchoolName());
    school.setEmail(req.getSchoolEmail());
    school.setPhone(req.getSchoolPhone());
    school.setAddress(req.getSchoolAddress());
    school.setActive(true);
    school = schoolRepository.save(school);

    SchoolMembership sm = new SchoolMembership();
    sm.setUser(userRepository.getReferenceById(userId));
    sm.setSchool(school);
    sm.setRole(ERole.ROLE_ADMIN);
    sm.setActive(true);
    membershipRepository.save(sm);

    return ResponseEntity.ok(Map.of(
        "id", school.getId(),
        "name", school.getName(),
        "email", school.getEmail(),
        "phone", school.getPhone(),
        "address", school.getAddress()
    ));
  }

  // -------- LIST MY SCHOOLS --------
  @GetMapping("/me/schools")
  public ResponseEntity<?> listMySchools() {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = principal.getId();

    var memberships = membershipRepository.findActiveWithSchoolByUserId(userId);
    List<Map<String, Object>> content = memberships.stream().map(sm -> {
      var s = sm.getSchool();
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", s.getId());
      m.put("name", s.getName());
      m.put("email", s.getEmail());
      m.put("phone", s.getPhone());
      m.put("address", s.getAddress());
      m.put("role", sm.getRole().name());
      return m;
    }).toList();

    return ResponseEntity.ok(Map.of("content", content, "size", content.size()));
  }

  // -------- SWITCH ACTIVE SCHOOL (new JWT) --------
  @PostMapping("/switch-school")
  public ResponseEntity<?> switchSchool(@RequestBody SwitchSchoolRequest req) {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = principal.getId();

    var membership = membershipRepository.findByUserIdAndSchoolIdAndActiveTrue(userId, req.getSchoolId())
        .orElse(null);
    if (membership == null) {
      return ResponseEntity.status(403).body(new MessageRequestResponse("Not a member of that school."));
    }

    var effective = authorityService.computeEffectiveAuthorities(userId, req.getSchoolId());
    String jwt = jwtUtils.generateJwtTokenForPhone(principal.getPhone(), effective, req.getSchoolId());

    List<String> roles = principal.getAuthorities().stream().map(a -> a.getAuthority()).toList();
    return ResponseEntity.ok(new JwtResponse(jwt, userId, principal.getUsername(),
        principal.getEmail(), principal.getPhone(), roles, new ArrayList<>(effective)));
  }

  // -------- FORGOT/RESET PASSWORD --------
  @PostMapping("/forgot-password")
  @Transactional
  public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
    var user = userRepository.findByEmail(req.getEmail()).orElse(null);
    if (user == null) {
      return ResponseEntity.ok(new MessageRequestResponse("If this email exists, we sent a reset link."));
    }
    String token = Tokens.randomUrlToken(48);
    String tokenHash = Tokens.sha256Hex(token);

    var t = new AuthToken();
    t.setUser(user);
    t.setTokenType(TokenType.PASSWORD_RESET);
    t.setTokenHash(tokenHash);
    t.setStatus(TokenStatus.PENDING);
    t.setExpiresAt(LocalDateTime.now().plusHours(2));
    tokenRepository.save(t);

    String resetUrl = frontendBase + "/reset-password?token=" + token;
    emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
    return ResponseEntity.ok(new MessageRequestResponse("If this email exists, we sent a reset link."));
  }

  @PostMapping("/reset-password")
  @Transactional
  public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
    String tokenHash = Tokens.sha256Hex(req.getToken());
    var t = tokenRepository.findByTokenHashAndTokenTypeAndStatus(
        tokenHash, TokenType.PASSWORD_RESET, TokenStatus.PENDING)
        .orElseThrow(() -> new RuntimeException("Invalid or already used reset token"));

    if (t.getExpiresAt().isBefore(LocalDateTime.now()))
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Reset link expired. Please request a new one."));

    var user = t.getUser();
    user.setPassword(encoder.encode(req.getNewPassword()));
    userRepository.save(user);

    t.setStatus(TokenStatus.USED);
    t.setUsedAt(LocalDateTime.now());
    tokenRepository.save(t);

    return ResponseEntity.ok(new MessageRequestResponse("Password updated. You can sign in now."));
  }

  // ------------------------ helpers ------------------------
  private boolean userHasRole(User u, ERole role) {
    return u.getRoles() != null && u.getRoles().stream().anyMatch(r -> r.getName() == role);
  }

  /**
   * Ensures the user has an active AccountSubscription.
   * - If none exists, creates a 15-day TRIAL starting now.
   * - TRIAL is active until trialEnd.
   * - PAID is active when activeUntil is null (open-ended) or in the future.
   */
  private boolean ensureAccountSubscriptionActive(User user) {
    var subOpt = accountSubscriptionRepository.findByUserId(user.getId());
    AccountSubscription sub = subOpt.orElseGet(() -> {
      var a = new AccountSubscription();
      a.setUser(user);
      a.setStatus(SubscriptionStatus.TRIAL);
      a.setTrialStart(LocalDateTime.now());
      a.setTrialEnd(LocalDateTime.now().plusDays(15));
      return accountSubscriptionRepository.save(a);
    });

    var now = LocalDateTime.now();

    if (sub.getStatus() == SubscriptionStatus.TRIAL) {
      return sub.getTrialEnd() != null && !now.isAfter(sub.getTrialEnd());
    }
    if (sub.getStatus() == SubscriptionStatus.PAID) {
      // open-ended if activeUntil == null
      return sub.getActiveUntil() == null || !now.isAfter(sub.getActiveUntil());
    }
    return false;
  }
}
