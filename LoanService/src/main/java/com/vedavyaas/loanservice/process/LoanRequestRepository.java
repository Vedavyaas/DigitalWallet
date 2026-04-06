package com.vedavyaas.loanservice.process;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRequestRepository extends JpaRepository<LoanRequestEntity, Long> {
    LoanRequestEntity findByUsername(String username);

    boolean existsByUsername(String username);
}
