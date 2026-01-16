package com.vedavyaas.walletservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    WalletEntity findByUsername(String user);

    boolean existsByUsername(String username);
}
