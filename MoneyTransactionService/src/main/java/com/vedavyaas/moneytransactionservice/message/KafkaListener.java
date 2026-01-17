package com.vedavyaas.moneytransactionservice.message;

import com.vedavyaas.moneytransactionservice.assets.TransactionResponse;
import com.vedavyaas.moneytransactionservice.repository.TransactionEntity;
import com.vedavyaas.moneytransactionservice.repository.TransactionRepository;
import com.vedavyaas.moneytransactionservice.transaction.TransactionService;
import org.springframework.stereotype.Component;

@Component
public class KafkaListener {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public KafkaListener(TransactionRepository transactionRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "user-registration", groupId = "transactionGroup")
    public void messageListener(String username) {
        if (!transactionRepository.existsByUsername(username)) {
            TransactionEntity transactionEntity = new TransactionEntity(username);
            transactionRepository.save(transactionEntity);
        }
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "transaction-response", groupId = "transactionGroup")
    public void transactionListener(String message){
        transactionService.completeTransaction(message);
    }
}