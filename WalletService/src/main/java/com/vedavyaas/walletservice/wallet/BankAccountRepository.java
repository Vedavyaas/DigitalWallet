package com.vedavyaas.walletservice.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
    boolean existsByAccountNumber(String accountNumber);
}
