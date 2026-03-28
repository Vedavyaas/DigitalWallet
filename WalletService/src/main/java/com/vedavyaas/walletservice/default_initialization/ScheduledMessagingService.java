package com.vedavyaas.walletservice.default_initialization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class ScheduledMessagingService {
    private final DefaultMessageRepository defaultMessageRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ScheduledMessagingService(DefaultMessageRepository defaultMessageRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.defaultMessageRepository = defaultMessageRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 10_000)
    public void sendDefaultBankAccountMessage() {
        Pageable pageable = PageRequest.of(0,10);
        Page<DefaultMessageEntity> page;

        do {
            page = defaultMessageRepository.findByUpdated(false, pageable);
            for (var i : page) {
                String data = i.getUsername() + "," + i.getDefaultAccountNumber();
                kafkaTemplate.send("balance-initialization", data);
                i.setUpdated(true);
            }
        }while (page.hasNext());

        page = defaultMessageRepository.findByUpdated(true, pageable);
        defaultMessageRepository.deleteAll(page);
    }
}
