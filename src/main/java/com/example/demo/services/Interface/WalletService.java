package com.example.demo.services.Interface;

import com.example.demo.dto.response.WalletResponse;
import com.example.demo.models.Wallet;

public interface WalletService {
	WalletResponse createDefaultWalletForUser(Long userId);

    WalletResponse getWalletById(Long walletId);

    Wallet getWalletEntityByUserId(Long userId);
    
    WalletResponse getWalletForCurrentUser(Long userId);
  
}
