package com.vedavyaas.walletservice.service;

import com.vedavyaas.walletservice.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class IOService {
    private final WalletRepository walletRepository;
    private final HistoryRepository historyRepository;

    public IOService(WalletRepository walletRepository, HistoryRepository historyRepository) {
        this.walletRepository = walletRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    @LogAnnotation(action = "Deposited amount: ")
    public String deposit(String user, BigDecimal amount) {
        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByUsername(user);

        //Almost impossible in perfect workflow
        if(optionalWalletEntity.isEmpty()) return "User not found";

        WalletEntity walletEntity = optionalWalletEntity.get();
        walletEntity.setBalance(walletEntity.getBalance().add(amount));
        walletRepository.save(walletEntity);

        return "Deposited successfully. Current balance is " + walletEntity.getBalance();
    }

    @Transactional
    @LogAnnotation(action = "Withdrawn amount: ")
    public String withdraw(String user, BigDecimal amount) {
        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByUsername(user);

        //Almost impossible in perfect workflow
        if(optionalWalletEntity.isEmpty()) return "User not found";

        WalletEntity walletEntity = optionalWalletEntity.get();
        if (walletEntity.getBalance().compareTo(amount) < 0) {
            return "Withdrawal failed. Current balance is " + walletEntity.getBalance();
        }

        walletEntity.setBalance(walletEntity.getBalance().subtract(amount));
        walletRepository.save(walletEntity);
        return "Withdrawn successfully. Current balance is " + walletEntity.getBalance();
    }

    @LogAnnotation(action = "Balance check")
    public String checkBalance(String user) {
        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByUsername(user);

        //Almost impossible in perfect workflow
        if(optionalWalletEntity.isEmpty()) return "User not found";

        WalletEntity walletEntity = optionalWalletEntity.get();
        return "Current balance in your account is: " + walletEntity.getBalance().toString();
    }

    public List<HistoryEntity> getHistory(String user) {
        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByUsername(user);
        return optionalWalletEntity.map(historyRepository::findHistoryEntitiesByUser).orElse(null);
    }
}
