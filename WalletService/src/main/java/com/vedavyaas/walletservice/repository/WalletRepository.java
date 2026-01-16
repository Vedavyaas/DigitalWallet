package com.vedavyaas.walletservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    WalletEntity findByUsername(String user);

    boolean existsByUsername(String username);
}
