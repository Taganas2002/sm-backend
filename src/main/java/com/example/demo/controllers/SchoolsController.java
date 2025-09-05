package com.example.demo.controllers;

import com.example.demo.dto.request.UpdateSchoolRequest;
import com.example.demo.models.ERole;
import com.example.demo.repository.SchoolMembershipRepository;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.security.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/schools")
public class SchoolsController {

  private final SchoolRepository schoolRepo;
  private final SchoolMembershipRepository membershipRepo;

  public SchoolsController(SchoolRepository schoolRepo,
                           SchoolMembershipRepository membershipRepo) {
    this.schoolRepo = schoolRepo;
    this.membershipRepo = membershipRepo;
  }

  @PatchMapping("/{schoolId}")
  @Transactional
  public ResponseEntity<?> update(@PathVariable Long schoolId,
                                  @RequestBody UpdateSchoolRequest req) {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Long userId = principal.getId();

    var member = membershipRepo.findByUserIdAndSchoolIdAndActiveTrue(userId, schoolId).orElse(null);
    if (member == null || member.getRole() != ERole.ROLE_ADMIN) {
      return ResponseEntity.status(403).body(Map.of("message", "Admin role required for this school."));
    }

    var s = schoolRepo.findById(schoolId).orElseThrow();
    if (req.getName() != null) s.setName(req.getName());
    if (req.getEmail() != null) s.setEmail(req.getEmail());
    if (req.getPhone() != null) s.setPhone(req.getPhone());
    if (req.getAddress() != null) s.setAddress(req.getAddress());
    schoolRepo.save(s);

    return ResponseEntity.ok(Map.of(
        "id", s.getId(),
        "name", s.getName(),
        "email", s.getEmail(),
        "phone", s.getPhone(),
        "address", s.getAddress()
    ));
  }
}
