package com.vedavyaas.moneytransactionservice.message;

import com.vedavyaas.moneytransactionservice.assets.TransactionDetails;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessagePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessagePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransactionMessage(String message) {
        kafkaTemplate.send("transaction-request", message);
    }
}
