package com.vedavyaas.walletservice.bank;

import com.vedavyaas.walletservice.wallet.BankAccountEntity;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/get")
    public String get(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getSubject();
    }

    @PostMapping("/bank/account/register")
    public ResponseEntity<String> registerBankAccount(@RequestBody BankAccountRegistration bankAccountRegistration, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bankAccountService.registerBankAccount(jwt.getSubject(), bankAccountRegistration));
    }

    @GetMapping("/get/bank/account")
    public ResponseEntity<List<BankAccountEntity>> bankAccountEntityResponseEntity(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bankAccountService.findAllBankAccountsRegistered(jwt.getSubject()));
    }

    @PostMapping("/register/default/account")
    public ResponseEntity<String> defaultBankAccountSelection(@RequestParam Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bankAccountService.registerDefaultBankAccount(id, jwt.getSubject()));
    }

    @PostMapping("/bank/account/verify")
    public ResponseEntity<String> bankAccountVerification(@RequestParam Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bankAccountService.verifyBankAccount(id, jwt.getSubject()));
    }

    @ExceptionHandler(DuplicateCredentialsException.class)
    public ResponseEntity<String> handle(DuplicateCredentialsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(com.vedavyaas.walletservice.bank.InvalidCredentialsException.class)
    public ResponseEntity<String> handle(InvalidCredentialsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(VerificationPendingException.class)
    public ResponseEntity<String> handle(VerificationPendingException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
