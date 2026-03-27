package com.vedavyaas.walletservice.bank;

public class DuplicateCredentialsException extends RuntimeException {
    public DuplicateCredentialsException(String message) {
        super(message);
    }
}
