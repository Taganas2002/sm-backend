package com.example.demo.models;

import com.example.demo.models.enums.TokenStatus;
import com.example.demo.models.enums.TokenType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens")
public class AuthToken {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false) @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne @JoinColumn(name = "school_id")
  private School school; // optional (we use it for post-verify auto-scope)

  @Enumerated(EnumType.STRING)
  @Column(name = "token_type", nullable = false, length = 30)
  private TokenType tokenType;

  @Column(name = "token_hash", nullable = false, length = 128, unique = true)
  private String tokenHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TokenStatus status = TokenStatus.PENDING;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "used_at")
  private LocalDateTime usedAt;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public School getSchool() { return school; }
  public void setSchool(School school) { this.school = school; }
  public TokenType getTokenType() { return tokenType; }
  public void setTokenType(TokenType tokenType) { this.tokenType = tokenType; }
  public String getTokenHash() { return tokenHash; }
  public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
  public TokenStatus getStatus() { return status; }
  public void setStatus(TokenStatus status) { this.status = status; }
  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUsedAt() { return usedAt; }
  public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
}
