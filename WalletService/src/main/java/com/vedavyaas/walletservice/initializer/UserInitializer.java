package com.vedavyaas.walletservice.initializer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserInitializer {
    private final UserDataInitialization userDataInitialization;

    public UserInitializer(UserDataInitialization userDataInitialization) {
        this.userDataInitialization = userDataInitialization;
    }

    @KafkaListener(topics = "user-registration", groupId = "walletGroup")
    public void registerUsers(String messages) {
        String[] message = messages.split(",");
        userDataInitialization.initialize(message[0], message[1]);
    }
}
