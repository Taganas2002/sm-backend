// src/main/java/com/example/demo/security/jwt/AuthTokenFilter.java
package com.example.demo.security.jwt;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.security.services.UserDetailsServiceImpl;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  private final JwtUtils jwtUtils;
  private final UserDetailsServiceImpl userDetailsService;

  public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
    this.jwtUtils = jwtUtils;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String phone = jwtUtils.getUserPhoneFromJwtToken(jwt);
        Long schoolId = jwtUtils.getSchoolIdFromJwt(jwt);

        // roles from DB (e.g., ROLE_SUPER_ADMIN / ROLE_ADMIN / …)
        UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
        Set<GrantedAuthority> merged = new HashSet<>(userDetails.getAuthorities());

        // plus per-school permissions coming from the token
        Set<String> tokenPerms = jwtUtils.getAuthoritiesFromJwt(jwt);
        merged.addAll(tokenPerms.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));

        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, merged);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        if (schoolId != null) request.setAttribute("jwt.schoolId", schoolId);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e.toString());
    }

    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer "))
      return headerAuth.substring(7);
    return null;
  }
}
