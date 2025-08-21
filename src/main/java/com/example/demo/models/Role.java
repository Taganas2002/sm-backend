package com.example.demo.models;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 30, nullable = false, unique = true)
  private ERole name;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  private Set<Permission> permissions = new LinkedHashSet<>();

  public Role() { }

  public Role(ERole name) {
    this.name = name;
  }

  // -------- getters / setters --------
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  public ERole getName() {
    return name;
  }
  public void setName(ERole name) {
    this.name = name;
  }

  public Set<Permission> getPermissions() {
    return permissions;
  }

  /**
   * Replace the entire permission set (safe even if collection is LAZY).
   * We copy into the existing collection to avoid replacing the Hibernate wrapper.
   */
  public void setPermissions(Set<Permission> permissions) {
    this.permissions.clear();
    if (permissions != null) {
      this.permissions.addAll(permissions);
    }
  }

  // helpers (optional)
  public void addPermission(Permission p) {
    if (p != null) this.permissions.add(p);
  }
  public void clearPermissions() {
    this.permissions.clear();
  }

  // equality by id to keep Set<Permission>/Set<Role> behavior sane
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role)) return false;
    Role role = (Role) o;
    return id != null && id.equals(role.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}