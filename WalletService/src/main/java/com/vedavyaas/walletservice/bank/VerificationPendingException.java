package com.vedavyaas.walletservice.bank;

public class VerificationPendingException extends RuntimeException {
    public VerificationPendingException(String message) {
        super(message);
    }
}
