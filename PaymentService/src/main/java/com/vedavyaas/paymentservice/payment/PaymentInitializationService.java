package com.vedavyaas.paymentservice.payment;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentInitializationService {
    private final PaymentRepository paymentRepository;

    public PaymentInitializationService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @KafkaListener(topics = "balance-initialization", groupId = "paymentGroup")
    public void initializeWallet(String messages) {
        String[] message = messages.split(",");
        if (paymentRepository.existsByUsername(message[0])) {
            List<PaymentEntity> payment = paymentRepository.findByUsername(message[0]);
            for (var i : payment) {
                if (i.getCurrentState().equals(State.CURRENT) && i.getAccountNumber().equals(message[1])) return;
                else if (i.getCurrentState().equals(State.CURRENT)) i.setCurrentState(State.INACTIVE);
            }
            paymentRepository.saveAll(payment);
        }

        PaymentEntity payment = new PaymentEntity(message[0], message[1]);
        payment.setCurrentState(State.CURRENT);
        paymentRepository.save(payment);
    }
}