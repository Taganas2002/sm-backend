package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", 
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "email"),
      @UniqueConstraint(columnNames = "phone")
    })
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;
  
  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified = false;
  
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private java.util.Set<SchoolMembership> memberships = new java.util.HashSet<>();

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 20)
  private String phone;

  @NotBlank
  @Size(max = 120)
  private String password;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", 
             joinColumns = @JoinColumn(name = "user_id"), 
             inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  // URL to the profile photo
  @Column(name = "profile_photo", nullable = true)
  private String profilePhoto;

  // Optional gender field
  @Column(name = "gender", nullable = true)
  private String gender;
  
  // New: PIN field (hashed)
  @Column(name = "pin", nullable = true)
  private String pin;

  // New: Failed PIN attempts counter
  @Column(name = "failed_pin_attempts", nullable = false)
  private int failedPinAttempts = 0;

  // Default constructor
  public User() {}

  // Constructor with required fields
  public User(String username, String email, String phone, String password) {
    this.username = username;
    this.email = email;
    this.phone = phone;
    this.password = password;
  }

  // Getters & Setters (including for new fields)
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPhone() {
    return phone;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public Set<Role> getRoles() {
    return roles;
  }
  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
  public String getProfilePhoto() {
    return profilePhoto;
  }
  public void setProfilePhoto(String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }
  public String getGender() {
    return gender;
  }
  public void setGender(String gender) {
    this.gender = gender;
  }
  public String getPin() {
    return pin;
  }
  public void setPin(String pin) {
    this.pin = pin;
  }
  public int getFailedPinAttempts() {
    return failedPinAttempts;
  }
  public void setFailedPinAttempts(int failedPinAttempts) {
    this.failedPinAttempts = failedPinAttempts;
  }
  public boolean isEmailVerified() { return emailVerified; }
  public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
  public java.util.Set<SchoolMembership> getMemberships() { return memberships; }
  public void setMemberships(java.util.Set<SchoolMembership> memberships) { this.memberships = memberships; }
}
