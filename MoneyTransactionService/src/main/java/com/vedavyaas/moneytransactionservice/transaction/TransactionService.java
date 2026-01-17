package com.vedavyaas.moneytransactionservice.transaction;

import com.vedavyaas.moneytransactionservice.assets.Status;
import com.vedavyaas.moneytransactionservice.assets.TransactionDetails;
import com.vedavyaas.moneytransactionservice.assets.TransactionResponse;
import com.vedavyaas.moneytransactionservice.message.KafkaMessagePublisher;
import com.vedavyaas.moneytransactionservice.repository.TransactionEntity;
import com.vedavyaas.moneytransactionservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final KafkaMessagePublisher kafkaMessagePublisher;

    public TransactionService(TransactionRepository transactionRepository, KafkaMessagePublisher kafkaMessagePublisher) {
        this.transactionRepository = transactionRepository;
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

    @Transactional
    public String processTransaction(String user, String toUser, BigDecimal amount) {
        if (!transactionRepository.existsByUsername(user)) return "User not found!";
        if (!transactionRepository.existsByUsername(toUser)) return "User not found for transaction!";

        TransactionEntity transactionEntity = transactionRepository.findByUsername(user);
        transactionEntity.setStatus(Status.PENDING);
        transactionRepository.save(transactionEntity);

        TransactionEntity transactionEntity1 = transactionRepository.findByUsername(toUser);
        transactionEntity1.setStatus(Status.PENDING);
        transactionRepository.save(transactionEntity1);

        String message = user + "," + toUser + "," + amount;

        kafkaMessagePublisher.publishTransactionMessage(message);
        return "Transaction pending";
    }

    public void completeTransaction(String message) {
        TransactionEntity transactionEntity = transactionRepository.findByUsername(message.split(",")[0]);
        String status = message.split(",")[1];
        Status statuss;
        if(status.equals("SUCCESS")) statuss = Status.SUCCESS;
        else statuss = Status.FAILURE;
        transactionEntity.setStatus(statuss);
        transactionRepository.save(transactionEntity);
    }

    public String getTransactionInfo(String user) {
        TransactionEntity transactionEntity = transactionRepository.findByUsername(user);

        if (transactionEntity.getStatus().equals(Status.NO_TRANSACTION)) return "No transaction done";

        else if (transactionEntity.getStatus().equals(Status.PENDING)) return "Transaction pending";

        else if (transactionEntity.getStatus().equals(Status.SUCCESS)) {
            transactionEntity.setStatus(Status.NO_TRANSACTION);
            transactionRepository.save(transactionEntity);
            return "Transaction successful";
        } else {
            transactionEntity.setStatus(Status.NO_TRANSACTION);
            transactionRepository.save(transactionEntity);
            return "Transaction failed retry.";
        }
    }
}
