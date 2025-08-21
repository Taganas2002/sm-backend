package com.example.demo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletResponse {
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal balance;
    private String walletType;
    private String walletStatus;
    private BigDecimal dailyLimit;
    private BigDecimal totalDeposit;
    private BigDecimal totalWithdrawal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

