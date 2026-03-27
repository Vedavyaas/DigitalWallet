package com.vedavyaas.verificationservice.verifier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDetailsRepository extends JpaRepository<BankDetailsEntity, Long> {
    Page<BankDetailsEntity> findByVerifiedIs(VerificationStatus verified, Pageable pageable);
}
