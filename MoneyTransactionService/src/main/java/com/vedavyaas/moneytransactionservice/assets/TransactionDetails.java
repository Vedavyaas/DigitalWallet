package com.vedavyaas.moneytransactionservice.assets;

import java.math.BigDecimal;

public record TransactionDetails(String user, String toUser, BigDecimal amount) {
}
