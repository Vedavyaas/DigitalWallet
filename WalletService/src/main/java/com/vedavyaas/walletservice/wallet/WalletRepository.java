package com.vedavyaas.walletservice.wallet;

import org.hibernate.boot.model.source.spi.Sortable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    boolean existsByUsernameAndEmail(String username, String email);

    WalletEntity findByUsername(String username);

    @Query("SELECT DISTINCT w FROM WalletEntity w JOIN w.bankAccounts b WHERE b.userUpdateRequest = :req AND b.updated = :upd AND b.verified = :ver")
    Page<WalletEntity> findAllPendingBankAccounts(@Param("req") boolean req, @Param("upd") boolean upd, @Param("ver") boolean ver, Pageable pageable);
}