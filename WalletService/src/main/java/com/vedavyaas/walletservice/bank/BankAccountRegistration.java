package com.vedavyaas.walletservice.bank;

import com.vedavyaas.walletservice.wallet.Bank;

public record BankAccountRegistration(String name, String accountNumber, String CIFNumber, String branch, String IFSCCode, Bank bankName, String accountType, String emailBank) {
}
