package com.vedavyaas.paymentservice.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    boolean existsByUsername(String username);

    List<PaymentEntity> findByUsername(String username);
}
