package com.vedavyaas.walletservice.service;

import com.vedavyaas.walletservice.repository.HistoryEntity;
import com.vedavyaas.walletservice.repository.HistoryRepository;
import com.vedavyaas.walletservice.repository.LogAnnotation;
import com.vedavyaas.walletservice.repository.WalletRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private final HistoryRepository historyRepository;
    private final WalletRepository walletRepository;

    public LogAspect(HistoryRepository historyRepository, WalletRepository walletRepository) {
        this.historyRepository = historyRepository;
        this.walletRepository = walletRepository;
    }

    @AfterReturning(value = "@annotation(logAnnotation)", argNames = "joinPoint,logAnnotation")
    public void logIntercept(JoinPoint joinPoint, LogAnnotation logAnnotation) {
        var args = joinPoint.getArgs();
        StringBuilder message = new StringBuilder(logAnnotation.action());

        if (args.length == 2) message.append(args[1]);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user == null) user = "anonymous";

        HistoryEntity historyEntity = new HistoryEntity(walletRepository.findByUsername(user).get(), message.toString());
        historyRepository.save(historyEntity);
    }
}