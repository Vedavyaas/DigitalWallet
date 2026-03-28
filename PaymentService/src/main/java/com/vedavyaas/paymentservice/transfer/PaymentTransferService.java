package com.vedavyaas.paymentservice.transfer;

import com.vedavyaas.paymentservice.payment.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentTransferService {
    private final PaymentRepository paymentRepository;

    public PaymentTransferService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
}
