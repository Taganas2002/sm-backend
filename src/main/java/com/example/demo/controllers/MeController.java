package com.example.demo.controllers;

import com.example.demo.repository.SchoolMembershipRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.security.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/me")
public class MeController {

  private final SchoolMembershipRepository membershipRepository;
  private final SubscriptionRepository subscriptionRepository;

  public MeController(SchoolMembershipRepository membershipRepository,
                      SubscriptionRepository subscriptionRepository) {
    this.membershipRepository = membershipRepository;
    this.subscriptionRepository = subscriptionRepository;
  }

  @GetMapping("/schools")
  public ResponseEntity<?> listMySchools() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var principal = (UserDetailsImpl) auth.getPrincipal();
    Long userId = principal.getId();

    var memberships = membershipRepository.findActiveWithSchoolByUserId(userId);

    List<Map<String, Object>> content = memberships.stream().map(sm -> {
      var school = sm.getSchool();
      var subOpt = subscriptionRepository.findBySchoolId(school.getId());

      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", school.getId());
      m.put("name", school.getName());
      m.put("email", school.getEmail());
      m.put("phone", school.getPhone());
      m.put("address", school.getAddress());
      m.put("role", sm.getRole().name());
      subOpt.ifPresent(sub -> {
        m.put("subscriptionStatus", sub.getStatus().name());
        m.put("trialEnd", sub.getTrialEnd());
        m.put("paidUntil", sub.getPaidUntil());
      });
      return m;
    }).toList();

    return ResponseEntity.ok(Map.of("content", content, "size", content.size()));
  }
}
