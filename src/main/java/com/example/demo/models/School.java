package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "schools")
public class School {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 160)
  private String name;

  @Column(length = 255) private String address;
  @Column(length = 60)  private String phone;
  @Column(length = 160) private String email;

  @Column(nullable = false)
  private Boolean active = true;

  // getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public Boolean getActive() { return active; }
  public void setActive(Boolean active) { this.active = active; }
}
