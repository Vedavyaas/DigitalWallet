package com.vedavyaas.walletservice.verify;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationRepository extends JpaRepository<VerificationEntity, Long> {
    VerificationEntity findByUsernameAndAccountNumber(String username, String accountNumber);

    boolean existsByUsernameAndAccountNumber(String username, String accountNumber);
}
