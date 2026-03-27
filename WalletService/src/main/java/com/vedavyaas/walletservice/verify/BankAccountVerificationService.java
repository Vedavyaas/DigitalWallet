package com.vedavyaas.walletservice.verify;

import com.vedavyaas.walletservice.wallet.BankAccountEntity;
import com.vedavyaas.walletservice.wallet.BankAccountRepository;
import com.vedavyaas.walletservice.wallet.WalletEntity;
import com.vedavyaas.walletservice.wallet.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BankAccountVerificationService {

    private final WalletRepository walletRepository;
    private final BankAccountRepository bankAccountRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final VerificationRepository verificationRepository;

    public BankAccountVerificationService(WalletRepository walletRepository, BankAccountRepository bankAccountRepository, KafkaTemplate<String, String> kafkaTemplate, VerificationRepository verificationRepository) {
        this.walletRepository = walletRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.verificationRepository = verificationRepository;
    }

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishBankDetails() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WalletEntity> walletEntities;

        do {
            walletEntities = walletRepository.findAllPendingBankAccounts(true, false, false, pageable);
            for (WalletEntity wallet : walletEntities) {
                List<BankAccountEntity> bankAccounts = wallet.getBankAccounts();
                for (BankAccountEntity account : bankAccounts) {
                    if (!account.isUpdated() && account.isUserUpdateRequest() && !account.isVerified()) {
                        sendBankAccountVerification(wallet, account);
                        account.setUpdated(true);
                        bankAccountRepository.save(account);
                        VerificationEntity verificationEntity;
                        if (!verificationRepository.existsByUsernameAndAccountNumber(wallet.getUsername(), account.getAccountNumber())) {
                            verificationEntity = new VerificationEntity(wallet.getUsername(), account.getAccountNumber());
                        } else {
                            verificationEntity = verificationRepository.findByUsernameAndAccountNumber(wallet.getUsername(), account.getAccountNumber());
                        }
                        verificationEntity.setVerificationStatus(VerificationStatus.PENDING);
                        verificationRepository.save(verificationEntity);
                    }
                }
            }
        } while (!walletEntities.isEmpty());
    }

    public void sendBankAccountVerification(WalletEntity wallet, BankAccountEntity bankAccountEntity) {
        String payload = wallet.getUsername() + "," + wallet.getEmail() + "," + 
                         bankAccountEntity.getName() + "," + 
                         bankAccountEntity.getAccountNumber() + "," + 
                         bankAccountEntity.getCIFNumber() + "," + 
                         bankAccountEntity.getBranch() + "," + 
                         bankAccountEntity.getIFSCCode() + "," + 
                         bankAccountEntity.getBankName() + "," + 
                         bankAccountEntity.getAccountType() + "," + 
                         bankAccountEntity.getEmailBank();
        System.out.println("================= KAFKA MESSAGE ====================");
        System.out.println("Sending verification message: " + payload);
        System.out.println("Topic: bank-account-verification");
        System.out.println("====================================================");
        kafkaTemplate.send("bank-account-verification", payload);
    }

    @KafkaListener(topics = "bank-account-verification-reply", groupId = "walletGroup")
    @Transactional
    public void checkAndSetVerification(String messages){
        //messages has username,account number,verified as true or false
        String[] message = messages.split(",");

        if (message[2].equals("true")) {
            WalletEntity walletEntity = walletRepository.findByUsername(message[0]);
            List<BankAccountEntity> bankAccountEntities = walletEntity.getBankAccounts();

            for (var i : bankAccountEntities) {
                if (i.getAccountNumber().equals(message[1])) {
                    VerificationEntity entity = verificationRepository.findByUsernameAndAccountNumber(message[0], message[1]);
                    entity.setVerificationStatus(VerificationStatus.VERIFIED);
                    verificationRepository.save(entity);
                    i.setVerified(true);
                    bankAccountRepository.save(i);
                    break;
                }
            }
        } else {
            VerificationEntity entity = verificationRepository.findByUsernameAndAccountNumber(message[0], message[1]);
            entity.setVerificationStatus(VerificationStatus.FAILED);
            verificationRepository.save(entity);
        }
    }
}