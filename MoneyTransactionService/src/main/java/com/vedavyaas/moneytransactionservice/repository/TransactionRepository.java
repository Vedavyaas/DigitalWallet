package com.vedavyaas.moneytransactionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    boolean existsByUsername(String username);

    TransactionEntity findByUsername(String user);
}
