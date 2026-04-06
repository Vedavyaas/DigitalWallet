package com.vedavyaas.loanservice.process;

public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(String message) {
        super(message);
    }
}
