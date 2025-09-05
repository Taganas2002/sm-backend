package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "school_memberships",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","school_id"}))
public class SchoolMembership {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "school_id")
  private School school;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private ERole role;  // ROLE_ADMIN, ROLE_TEACHER, ...

  @Column(nullable = false)
  private boolean active = true;

  // -------- getters / setters --------
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public School getSchool() { return school; }
  public void setSchool(School school) { this.school = school; }

  public ERole getRole() { return role; }
  public void setRole(ERole role) { this.role = role; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
}
