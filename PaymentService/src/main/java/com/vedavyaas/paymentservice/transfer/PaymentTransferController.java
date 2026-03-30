package com.vedavyaas.paymentservice.transfer;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class PaymentTransferController {
    private final PaymentTransferService paymentTransferService;

    public PaymentTransferController(PaymentTransferService paymentTransferService) {
        this.paymentTransferService = paymentTransferService;
    }

    @GetMapping("/get/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(paymentTransferService.getBalance(jwt.getSubject()));
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<String> depositMoney(@RequestParam BigDecimal amount, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(paymentTransferService.deposit(jwt.getSubject(), amount));
    }

    @PostMapping("/account/withdraw")
    public ResponseEntity<String> withdrawMoney(@RequestParam BigDecimal amount, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(paymentTransferService.withdraw(jwt.getSubject(), amount));
    }

    @PostMapping("/transfer/money")
    public ResponseEntity<String> transferMoney(@RequestParam BigDecimal amount, @RequestParam String toUser, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(paymentTransferService.transfer(jwt.getSubject(), toUser, amount));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handle(InsufficientBalanceException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<String> handle(InvalidAccountException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
