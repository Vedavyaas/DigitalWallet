package com.vedavyaas.moneytransactionservice.message;

import com.vedavyaas.moneytransactionservice.assets.TransactionDetails;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessagePublisher {

    private final KafkaTemplate<String, TransactionDetails> kafkaTemplate;

    public KafkaMessagePublisher(KafkaTemplate<String, TransactionDetails> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransactionMessage(TransactionDetails transactionDetails) {
        kafkaTemplate.send("transaction-request", transactionDetails);
    }
}
