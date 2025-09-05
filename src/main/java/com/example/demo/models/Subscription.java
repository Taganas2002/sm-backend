package com.example.demo.models;

import com.example.demo.models.base.Auditable;
import com.example.demo.models.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "school_id")
  private School school;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private SubscriptionStatus status = SubscriptionStatus.TRIAL;

  @Column(name = "trial_start")
  private LocalDateTime trialStart;

  @Column(name = "trial_end")
  private LocalDateTime trialEnd;

  @Column(name = "paid_until")
  private LocalDateTime paidUntil;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public School getSchool() { return school; }
  public void setSchool(School school) { this.school = school; }
  public SubscriptionStatus getStatus() { return status; }
  public void setStatus(SubscriptionStatus status) { this.status = status; }
  public LocalDateTime getTrialStart() { return trialStart; }
  public void setTrialStart(LocalDateTime trialStart) { this.trialStart = trialStart; }
  public LocalDateTime getTrialEnd() { return trialEnd; }
  public void setTrialEnd(LocalDateTime trialEnd) { this.trialEnd = trialEnd; }
  public LocalDateTime getPaidUntil() { return paidUntil; }
  public void setPaidUntil(LocalDateTime paidUntil) { this.paidUntil = paidUntil; }
}
