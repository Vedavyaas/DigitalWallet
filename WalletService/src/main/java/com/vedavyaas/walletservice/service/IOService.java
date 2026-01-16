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
        checkAndCreateAccount(user);

        WalletEntity walletEntity = walletRepository.findByUsername(user);
        walletEntity.setBalance(walletEntity.getBalance().add(amount));
        walletRepository.save(walletEntity);

        return "Deposited successfully. Current balance is " + walletEntity.getBalance();
    }

    @Transactional
    @LogAnnotation(action = "Withdrawn amount: ")
    public String withdraw(String user, BigDecimal amount) {
        checkAndCreateAccount(user);

        WalletEntity walletEntity = walletRepository.findByUsername(user);
        if (walletEntity.getBalance().compareTo(amount) < 0) {
            return "Withdrawal failed. Current balance is " + walletEntity.getBalance();
        }

        walletEntity.setBalance(walletEntity.getBalance().subtract(amount));
        walletRepository.save(walletEntity);
        return "Withdrawn successfully. Current balance is " + walletEntity.getBalance();
    }

    @LogAnnotation(action = "Balance check")
    public String checkBalance(String user) {
        checkAndCreateAccount(user);
        WalletEntity walletEntity = walletRepository.findByUsername(user);

        return "Current balance in your account is: " + walletEntity.getBalance().toString();
    }

    private void checkAndCreateAccount(String user) {
        if (!walletRepository.existsByUsername(user)) {
            WalletEntity walletEntityNew = new WalletEntity(user);
            walletRepository.save(walletEntityNew);
        }
    }

    public List<HistoryEntity> getHistory(String user) {
        return historyRepository.findHistoryEntitiesByUser((walletRepository.findByUsername(user)));
    }
}
