package com.example.demo.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.MessageRequestResponse;
import com.example.demo.models.ERole;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.request.SignupRequest;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;
import com.example.demo.services.Interface.WalletService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;
  
  @Autowired private com.example.demo.services.Interface.AuthorityService authorityService;
  

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    logger.info("Attempting to authenticate user with phone: {}", loginRequest.getPhone());
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      var userDetails = (UserDetailsImpl) authentication.getPrincipal();

      // roles from GrantedAuthorities (unchanged)
      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority()).collect(java.util.stream.Collectors.toList());

      // compute effective authorities and put into JWT + response
      var effective = authorityService.computeEffectiveAuthorities(userDetails.getId());
      String jwt = jwtUtils.generateJwtToken(authentication, effective);

      logger.info("User authenticated successfully: {}", userDetails.getPhone());
      return ResponseEntity.ok(new JwtResponse(jwt,
          userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getPhone(),
          roles, new java.util.ArrayList<>(effective)));
    } catch (Exception e) {
      logger.error("Authentication failed for phone: {}. Error: {}", loginRequest.getPhone(), e.getMessage());
      return ResponseEntity.badRequest().body(new com.example.demo.dto.response.MessageRequestResponse(
        "Error: Invalid phone or password!"));
    }
  }


  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    logger.info("Attempting to register user with phone: {}", signUpRequest.getPhone());

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      logger.warn("Registration failed: Email already in use - {}", signUpRequest.getEmail());
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Error: Email is already in use!"));
    }

    if (userRepository.existsByPhone(signUpRequest.getPhone())) {
      logger.warn("Registration failed: Phone number already in use - {}", signUpRequest.getPhone());
      return ResponseEntity.badRequest().body(new MessageRequestResponse("Error: Phone number is already in use!"));
    }

    User user = new User(
        signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        signUpRequest.getPhone(),
        encoder.encode(signUpRequest.getPassword())
    );

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);
            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
     User saveUser  =userRepository.save(user);
    logger.info("User registered successfully with Id: {}", saveUser.getId());
    return ResponseEntity.ok(new MessageRequestResponse("User registered successfully!"));
  }
}
