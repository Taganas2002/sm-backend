package com.example.demo.repository;

import com.example.demo.models.Subscription;
import com.example.demo.models.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
  List<Subscription> findByStatusAndTrialEndBefore(SubscriptionStatus status, LocalDateTime time);
  Optional<Subscription> findTopBySchoolIdOrderByIdDesc(Long schoolId);
  @Query("select s from Subscription s where s.school.id = :schoolId")
  Optional<Subscription> findBySchoolId(Long schoolId);
}
