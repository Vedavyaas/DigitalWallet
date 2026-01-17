package com.vedavyaas.walletservice.message;

import com.vedavyaas.walletservice.assets.TransactionResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {
    private final KafkaTemplate<String, TransactionResponse> kafkaTemplate;

    public MessagePublisher(KafkaTemplate<String, TransactionResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void messagePublisher(TransactionResponse transactionResponse) {
        kafkaTemplate.send("transaction-response", transactionResponse);
    }
}
