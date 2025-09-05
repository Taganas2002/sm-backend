package com.example.demo.controllers;

import com.example.demo.dto.request.ActivateAccountSubscriptionRequest;
import com.example.demo.models.AccountSubscription;
import com.example.demo.models.enums.SubscriptionStatus;
import com.example.demo.repository.AccountSubscriptionRepository;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionsController {

  private final AccountSubscriptionRepository accountSubRepo;
  private final UserRepository userRepo;

  public SubscriptionsController(AccountSubscriptionRepository accountSubRepo,
                                 UserRepository userRepo) {
    this.accountSubRepo = accountSubRepo;
    this.userRepo = userRepo;
  }

  /**
   * Super Admin: activate/extend an account's paid window by Y/M/D.
   * Body: { "userId": 16, "years": 1, "months": 0, "days": 0 }
   */
  @PostMapping("/activate-owner")
  @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
  @Transactional
  public ResponseEntity<?> activateOwner(@Valid @RequestBody ActivateAccountSubscriptionRequest req) {
    var target = userRepo.findById(req.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.getUserId()));

    var sub = accountSubRepo.findByUserId(req.getUserId())
        .orElseGet(() -> {
          var s = new AccountSubscription();
          s.setUser(target);
          s.setStatus(SubscriptionStatus.TRIAL);
          return s;
        });

    LocalDateTime base = sub.getActiveUntil();
    if (base == null || base.isBefore(LocalDateTime.now())) {
      base = LocalDateTime.now();
    }

    int y = Optional.ofNullable(req.getYears()).orElse(0);
    int m = Optional.ofNullable(req.getMonths()).orElse(0);
    int d = Optional.ofNullable(req.getDays()).orElse(0);

    LocalDateTime until = base.plusYears(y).plusMonths(m).plusDays(d)
        .truncatedTo(ChronoUnit.SECONDS);

    sub.setActiveUntil(until);
    sub.setStatus(SubscriptionStatus.PAID);
    accountSubRepo.save(sub);

    return ResponseEntity.ok(Map.of(
        "userId", target.getId(),
        "status", sub.getStatus().name(),
        "activeUntil", sub.getActiveUntil()
    ));
  }
}
