package com.example.demo.repository;

import com.example.demo.models.AuthToken;
import com.example.demo.models.enums.TokenStatus;
import com.example.demo.models.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
  Optional<AuthToken> findByTokenHashAndTokenTypeAndStatus(String tokenHash, TokenType type, TokenStatus status);
}
