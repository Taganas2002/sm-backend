package com.example.demo.jobs;

import com.example.demo.models.Subscription;
import com.example.demo.models.enums.SubscriptionStatus;
import com.example.demo.repository.SubscriptionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TrialExpiryJob {
  private final SubscriptionRepository repo;
  public TrialExpiryJob(SubscriptionRepository repo) { this.repo = repo; }

  // every day at 01:00
  @Scheduled(cron = "0 0 1 * * *")
  public void expireTrials() {
    List<Subscription> toExpire = repo.findByStatusAndTrialEndBefore(SubscriptionStatus.TRIAL, LocalDateTime.now());
    for (Subscription s : toExpire) s.setStatus(SubscriptionStatus.EXPIRED);
    if (!toExpire.isEmpty()) repo.saveAll(toExpire);
  }
}
