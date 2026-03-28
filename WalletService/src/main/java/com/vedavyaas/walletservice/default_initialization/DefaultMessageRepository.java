package com.vedavyaas.walletservice.default_initialization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultMessageRepository extends JpaRepository<DefaultMessageEntity, Long> {
    Page<DefaultMessageEntity> findByUpdated(boolean updated, Pageable pageable);
}
