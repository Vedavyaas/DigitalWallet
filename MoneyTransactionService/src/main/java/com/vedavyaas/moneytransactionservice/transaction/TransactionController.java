package com.vedavyaas.moneytransactionservice.transaction;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction/send")
    public String sendTransaction(@RequestParam String toUser, @RequestParam BigDecimal amount){
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        return transactionService.processTransaction(user, toUser, amount);
    }

    @GetMapping("/get/status")
    public String getTransaction(){
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        return transactionService.getTransactionInfo(user);
    }
}
