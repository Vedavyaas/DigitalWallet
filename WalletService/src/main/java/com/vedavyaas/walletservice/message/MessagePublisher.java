package com.vedavyaas.walletservice.message;

import com.vedavyaas.walletservice.assets.TransactionResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MessagePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void messagePublisher(String message) {
        kafkaTemplate.send("transaction-response", message);
    }
}
