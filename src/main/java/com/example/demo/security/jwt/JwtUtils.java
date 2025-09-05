// src/main/java/com/example/demo/security/jwt/JwtUtils.java
package com.example.demo.security.jwt;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.demo.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${bezkoder.app.jwtSecret}")     private String jwtSecret;
  @Value("${bezkoder.app.jwtExpirationMs}") private long jwtExpirationMs;

  private Key key() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); }

  /** legacy (kept) */
  public String generateJwtToken(Authentication authentication, Set<String> authorities) {
    return buildFromAuth(authentication, authorities, null);
  }

  /** new — with active school id */
  public String generateJwtToken(Authentication authentication, Set<String> authorities, Long schoolId) {
    return buildFromAuth(authentication, authorities, schoolId);
  }

  /** manual mint by phone (used in verify/switch) */
  public String generateJwtTokenForPhone(String phone, Set<String> authorities, Long schoolId) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .setSubject(phone)
        .claim("authorities", authorities == null ? List.of() : authorities) // school perms
        // roles may be absent here; filter will load from DB anyway
        .setIssuedAt(now)
        .setExpiration(exp)
        .claim("schoolId", schoolId)
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  private String buildFromAuth(Authentication authentication, Set<String> authorities, Long schoolId) {
    var user = (UserDetailsImpl) authentication.getPrincipal();

    // Extract ROLE_* for visibility/debugging; filter still loads roles from DB
    List<String> roles = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(a -> a.startsWith("ROLE_"))
        .distinct()
        .toList();

    Date now = new Date();
    Date exp = new Date(now.getTime() + jwtExpirationMs);

    JwtBuilder b = Jwts.builder()
        .setSubject(user.getPhone())
        .claim("roles", roles)                                   // <-- add roles
        .claim("authorities", authorities == null ? List.of() : authorities) // school perms
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(key(), SignatureAlgorithm.HS256);

    if (schoolId != null) b.claim("schoolId", schoolId);
    return b.compact();
  }

  public String getUserPhoneFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  public Long getSchoolIdFromJwt(String token) {
    Claims c = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
    Object s = c.get("schoolId");
    if (s instanceof Number n) return n.longValue();
    try { return s != null ? Long.parseLong(s.toString()) : null; } catch (Exception e) { return null; }
  }

  /** read the school-scoped permission strings from the token */
  public Set<String> getAuthoritiesFromJwt(String token) {
    try {
      Claims c = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
      Object a = c.get("authorities");
      if (a instanceof Collection<?> col) {
        return col.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toSet());
      }
    } catch (Exception ignored) {}
    return Set.of();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      logger.error("JWT error: {}", e.getMessage());
      return false;
    }
  }
}
