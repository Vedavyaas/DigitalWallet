package com.vedavyaas.walletservice.assets;

import java.math.BigDecimal;

public record TransactionDetails(String user, String toUser, BigDecimal amount) {
}