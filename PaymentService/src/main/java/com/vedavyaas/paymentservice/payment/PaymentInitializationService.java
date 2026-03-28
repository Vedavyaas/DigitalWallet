package com.vedavyaas.paymentservice.payment;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentInitializationService {
    private final PaymentRepository paymentRepository;

    public PaymentInitializationService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @KafkaListener(topics = "balance-initialization", groupId = "paymentGroup")
    public void initializeWallet(String messages) {
        String[] message = messages.split(",");
        PaymentEntity payment = new PaymentEntity(message[0], message[1]);
        paymentRepository.save(payment);
    }
}