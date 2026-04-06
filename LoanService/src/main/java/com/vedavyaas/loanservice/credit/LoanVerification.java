package com.vedavyaas.loanservice.credit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoanVerification {
    @Scheduled(fixedDelay = 10_000)
    public void checkLoan() {

    }
}
