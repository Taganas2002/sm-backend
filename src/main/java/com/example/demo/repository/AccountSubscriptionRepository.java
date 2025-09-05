package com.example.demo.repository;

import com.example.demo.models.AccountSubscription;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSubscriptionRepository extends JpaRepository<AccountSubscription, Long> {

  Optional<AccountSubscription> findByUserId(Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from AccountSubscription a where a.user.id = :userId")
  Optional<AccountSubscription> findByUserIdForUpdate(Long userId);
}
