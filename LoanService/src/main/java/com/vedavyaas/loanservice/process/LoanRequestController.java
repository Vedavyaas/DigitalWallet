package com.vedavyaas.loanservice.process;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoanRequestController {
    private final LoanRequestService loanRequestService;

    public LoanRequestController(LoanRequestService loanRequestService) {
        this.loanRequestService = loanRequestService;
    }

    @GetMapping("/get/loans/active")
    public ResponseEntity<LoanRequestEntity> getActiveLoans(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(loanRequestService.getAllLoans(jwt.getSubject()));
    }

    @PostMapping("/apply/loan")
    public ResponseEntity<String> applyLoan(@RequestParam Double loanAmount, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(loanRequestService.applyLoan(jwt.getSubject(), loanAmount));
    }

    @PostMapping("/loan/cancel")
    public ResponseEntity<String> cancelLoan(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(loanRequestService.loanCancellationRequest(jwt.getSubject()));
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<String> handleDuplicateApplicationException(DuplicateApplicationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}