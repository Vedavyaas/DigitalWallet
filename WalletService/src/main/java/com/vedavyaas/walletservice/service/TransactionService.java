package com.vedavyaas.walletservice.service;

import com.vedavyaas.walletservice.assets.Status;
import com.vedavyaas.walletservice.assets.TransactionDetails;
import com.vedavyaas.walletservice.assets.TransactionResponse;
import com.vedavyaas.walletservice.message.MessagePublisher;
import com.vedavyaas.walletservice.repository.WalletEntity;
import com.vedavyaas.walletservice.repository.WalletRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final MessagePublisher messagePublisher;

    private final WalletRepository walletRepository;

    public TransactionService(MessagePublisher messagePublisher, WalletRepository walletRepository) {
        this.messagePublisher = messagePublisher;
        this.walletRepository = walletRepository;
    }

    @Transactional
    @CircuitBreaker(name = "transaction", fallbackMethod = "fallBackTransaction")
    public void transaction(TransactionDetails transactionDetails) {
        WalletEntity walletEntity = walletRepository.findByUsername(transactionDetails.user()).get();
        WalletEntity walletEntity1 = walletRepository.findByUsername(transactionDetails.toUser()).get();

        if (walletEntity.getBalance().compareTo(transactionDetails.amount()) <= 0) {
            messagePublisher.messagePublisher(new TransactionResponse(transactionDetails.user(), Status.FAILURE));
            return;
        }

        walletEntity.setBalance(walletEntity.getBalance().subtract(transactionDetails.amount()));
        walletEntity1.setBalance(walletEntity1.getBalance().add(transactionDetails.amount()));

        walletRepository.save(walletEntity);
        walletRepository.save(walletEntity1);
        messagePublisher.messagePublisher(new TransactionResponse(transactionDetails.user(), Status.SUCCESS));
    }

    public void fallBackTransaction(TransactionDetails transactionDetails, Throwable t) {
        //ignore throwable
        messagePublisher.messagePublisher(new TransactionResponse(transactionDetails.user(), Status.FAILURE));
    }
}
