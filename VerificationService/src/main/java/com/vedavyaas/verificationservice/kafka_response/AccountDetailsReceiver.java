package com.vedavyaas.verificationservice.kafka_response;

import com.vedavyaas.verificationservice.verifier.BankDetailsEntity;
import com.vedavyaas.verificationservice.verifier.BankDetailsRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AccountDetailsReceiver {

    private final BankDetailsRepository bankDetailsRepository;

    public AccountDetailsReceiver(BankDetailsRepository bankDetailsRepository) {
        this.bankDetailsRepository = bankDetailsRepository;
    }

    @KafkaListener(topics = "bank-account-verification", groupId = "verifierGroup")
    public void receiveAccountDetails(String messages) {
        //messages has wallet.getUsername() + "," + wallet.getEmail() + "," + 
        //                         bankAccountEntity.getName() + "," + 
        //                         bankAccountEntity.getAccountNumber() + "," + 
        //                         bankAccountEntity.getCIFNumber() + "," + 
        //                         bankAccountEntity.getBranch() + "," + 
        //                         bankAccountEntity.getIFSCCode() + "," + 
        //                         bankAccountEntity.getBankName() + "," + 
        //                         bankAccountEntity.getAccountType() + "," + 
        //                         bankAccountEntity.getEmailBank();
        String[] message = messages.split(",");
        BankDetailsEntity bankDetailsEntity = new BankDetailsEntity(message[0], message[1],
                message[3], message[2], message[4], message[5],
                message[6], message[7], message[8], message[9]);

        bankDetailsRepository.save(bankDetailsEntity);
    }
}
