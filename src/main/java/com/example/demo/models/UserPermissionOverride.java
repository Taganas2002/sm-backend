package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_permission_overrides",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","permission_id"}))
public class UserPermissionOverride {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "permission_id", nullable = false)
  private Permission permission;

  @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10)
  private Effect effect; // ALLOW or DENY

  public enum Effect { ALLOW, DENY }

  public UserPermissionOverride() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public Permission getPermission() { return permission; }
  public void setPermission(Permission permission) { this.permission = permission; }
  public Effect getEffect() { return effect; }
  public void setEffect(Effect effect) { this.effect = effect; }
}
