package com.vedavyaas.walletservice.verify;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationCheckerController {
    private final VerificationRepository verificationRepository;

    public VerificationCheckerController(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }

    @GetMapping("get/status")
    public ResponseEntity<VerificationStatus> getBankAccountVerificationStatus(@RequestParam String accountNumber, @AuthenticationPrincipal Jwt jwt) {
        VerificationEntity entity = verificationRepository.findByUsernameAndAccountNumber(jwt.getSubject(), accountNumber);
        if (entity == null) {
            return ResponseEntity.ok(VerificationStatus.PENDING);
        }
        return ResponseEntity.ok(entity.getVerificationStatus());
    }
}