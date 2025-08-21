package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many wallets belong to one User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String name;
   
    private BigDecimal balance;
    
    
    @Column(name = "wallet_type")
    private String walletType;       // e.g., "PERSONAL", "BUSINESS"
    
    @Column(name = "wallet_status")
    private String walletStatus;     // e.g., "ACTIVE", "SUSPENDED"
    
    @Column(name = "daily_limit")
    private BigDecimal dailyLimit;   // Daily maximum transaction amount
    
    @Column(name = "total_deposit")
    private BigDecimal totalDeposit; // Aggregated deposits
    
    @Column(name = "total_withdrawal")
    private BigDecimal totalWithdrawal; // Aggregated withdrawals
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
       createdAt = LocalDateTime.now();
       updatedAt = createdAt;
    }
    
    @PreUpdate
    public void preUpdate() {
       updatedAt = LocalDateTime.now();
    }
}
