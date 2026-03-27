package com.vedavyaas.walletservice.initializer;

import com.vedavyaas.walletservice.wallet.WalletEntity;
import com.vedavyaas.walletservice.wallet.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class UserDataInitialization {
    private final WalletRepository walletRepository;

    public UserDataInitialization(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void initialize(String username, String email) {
        if (walletRepository.existsByUsernameAndEmail(username, email)) return;
        WalletEntity walletEntity = new WalletEntity(username, email);
        walletRepository.save(walletEntity);
    }
}