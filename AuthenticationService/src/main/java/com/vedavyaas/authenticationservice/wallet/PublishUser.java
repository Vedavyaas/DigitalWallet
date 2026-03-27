package com.vedavyaas.authenticationservice.wallet;

import com.vedavyaas.authenticationservice.user.UserDetailsEntity;
import com.vedavyaas.authenticationservice.user.UserDetailsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PublishUser {
    private final UserDetailsRepository userDetailsRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PublishUser(UserDetailsRepository userDetailsRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.userDetailsRepository = userDetailsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 10_000)
    public void publishUnUpdatedUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDetailsEntity> userDetailsEntityDTOS;
        do {
            userDetailsEntityDTOS = userDetailsRepository.findByUpdated(false, pageable);
            for (var userDetailsEntityDTO : userDetailsEntityDTOS) {
                userDetailsEntityDTO.setUpdated(true);
                publishToKafka(userDetailsEntityDTO);
            }
            userDetailsRepository.saveAllAndFlush(userDetailsEntityDTOS);
        } while (userDetailsEntityDTOS.hasNext());
    }

    public void publishToKafka(UserDetailsEntity userDetailsEntity) {
        kafkaTemplate.send("user-registration", userDetailsEntity.getUsername()+","+userDetailsEntity.getEmail());
    }
}
