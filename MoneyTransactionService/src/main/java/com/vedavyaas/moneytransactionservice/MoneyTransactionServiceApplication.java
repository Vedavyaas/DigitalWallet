package com.vedavyaas.moneytransactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MoneyTransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyTransactionServiceApplication.class, args);
    }
}
