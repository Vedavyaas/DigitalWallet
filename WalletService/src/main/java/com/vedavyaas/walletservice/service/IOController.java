package com.vedavyaas.walletservice.service;

import com.vedavyaas.walletservice.repository.HistoryEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class IOController {
    private final IOService ioService;

    public IOController(IOService ioService) {
        this.ioService = ioService;
    }

    @PostMapping("/deposit")
    public String depositController(@RequestParam BigDecimal amount) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return ioService.deposit(user, amount);
    }

    @PostMapping("/withdraw")
    public String withdrawController(@RequestParam BigDecimal amount) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return ioService.withdraw(user, amount);
    }

    @GetMapping("/get/balance")
    public String getBalance() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return ioService.checkBalance(user);
    }

    @GetMapping("/get/history")
    public List<HistoryEntity> getHistory() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return ioService.getHistory(user);
    }
}
