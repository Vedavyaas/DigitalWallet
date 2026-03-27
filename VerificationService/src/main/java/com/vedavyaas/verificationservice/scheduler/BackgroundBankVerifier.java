package com.vedavyaas.verificationservice.scheduler;

import com.vedavyaas.verificationservice.verifier.BankDetailsEntity;
import com.vedavyaas.verificationservice.verifier.BankDetailsRepository;
import com.vedavyaas.verificationservice.verifier.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackgroundBankVerifier {
    private final BankDetailsRepository bankDetailsRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BackgroundBankVerifier(BankDetailsRepository bankDetailsRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.bankDetailsRepository = bankDetailsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 15_000)
    public void verifyBankAccounts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BankDetailsEntity> bankDetailsEntityPage;

        do {
            bankDetailsEntityPage = bankDetailsRepository.findByVerifiedIs(VerificationStatus.PENDING , pageable);
            for (var i : bankDetailsEntityPage) {

                if (!i.getEmail().equals(i.getUserEmailOnBank())) {
                    i.setVerified(VerificationStatus.FAILED);
                }

                //extra layers to be added for verification

                else {
                    i.setVerified(VerificationStatus.VERIFIED);
                    bankDetailsRepository.save(i);
                }
            }
        } while (bankDetailsEntityPage.hasNext());
    }

    @Scheduled(fixedDelay = 10_000)
    public void sendMessageOnVerification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BankDetailsEntity> bankDetailsEntityPage;

        do {
            bankDetailsEntityPage = bankDetailsRepository.findByVerifiedIs(VerificationStatus.VERIFIED, pageable);
            for (var i : bankDetailsEntityPage) {
                String data = i.getUsername() + "," + i.getAccountNumber() + "true";
                kafkaTemplate.send("bank-account-verification-reply", data);
                bankDetailsRepository.delete(i);
            }
        } while (bankDetailsEntityPage.hasNext());
    }

    @Scheduled(fixedDelay = 20_000)
    public void clearFailedVerification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BankDetailsEntity> bankDetailsEntityPage;

        do {
            bankDetailsEntityPage = bankDetailsRepository.findByVerifiedIs(VerificationStatus.FAILED, pageable);
            for (var i : bankDetailsEntityPage) {
                String data = i.getUsername() + "," + i.getAccountNumber() + "false";
                kafkaTemplate.send("bank-account-verification-reply", data);
                bankDetailsRepository.delete(i);
            }
        } while (bankDetailsEntityPage.hasNext());
    }
}