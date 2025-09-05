package com.example.demo.models;

import com.example.demo.models.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_subscriptions",
       uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class AccountSubscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 24)
  private SubscriptionStatus status = SubscriptionStatus.TRIAL;

  @Column(name = "trial_start")
  private LocalDateTime trialStart;

  @Column(name = "trial_end")
  private LocalDateTime trialEnd;

  // <-- THIS IS THE FIELD THE CONTROLLER USES
  @Column(name = "active_until")
  private LocalDateTime activeUntil;

  // getters / setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public SubscriptionStatus getStatus() { return status; }
  public void setStatus(SubscriptionStatus status) { this.status = status; }

  public LocalDateTime getTrialStart() { return trialStart; }
  public void setTrialStart(LocalDateTime trialStart) { this.trialStart = trialStart; }

  public LocalDateTime getTrialEnd() { return trialEnd; }
  public void setTrialEnd(LocalDateTime trialEnd) { this.trialEnd = trialEnd; }

  public LocalDateTime getActiveUntil() { return activeUntil; }
  public void setActiveUntil(LocalDateTime activeUntil) { this.activeUntil = activeUntil; }
}
