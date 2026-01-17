package com.vedavyaas.walletservice.message;

import com.vedavyaas.walletservice.assets.TransactionDetails;
import com.vedavyaas.walletservice.repository.WalletEntity;
import com.vedavyaas.walletservice.repository.WalletRepository;
import com.vedavyaas.walletservice.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class KafkaListener {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    public KafkaListener(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "user-registration", groupId = "walletGroup")
    public void registrationMessageListener(String username) {
        if(!walletRepository.existsByUsername(username)){
            WalletEntity walletEntity = new WalletEntity(username, new BigDecimal(0));
            walletRepository.save(walletEntity);
        }
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "transaction-request", groupId = "walletGroup")
    public void transactionMessageListener(String message){
        transactionService.transaction(message);
    }
}