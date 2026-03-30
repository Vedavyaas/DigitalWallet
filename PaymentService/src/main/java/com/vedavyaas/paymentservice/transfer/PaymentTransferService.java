package com.vedavyaas.paymentservice.transfer;

import com.vedavyaas.paymentservice.payment.PaymentEntity;
import com.vedavyaas.paymentservice.payment.PaymentRepository;
import com.vedavyaas.paymentservice.payment.State;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentTransferService {
    private final PaymentRepository paymentRepository;

    public PaymentTransferService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public BigDecimal getBalance(String username) {
        if (!paymentRepository.existsByUsername(username)) {
            throw new InvalidAccountException("Verify and register a default account to proceed.");
        }

        List<PaymentEntity> payment = paymentRepository.findByUsername(username);
        for (var i : payment) {
            if (i.getCurrentState().equals(State.CURRENT)) return i.getBalance();
        }

        return null;
    }

    public String deposit(String username, BigDecimal amount) {
        if (!paymentRepository.existsByUsername(username)) {
            throw new InvalidAccountException("Verify and register a default account to proceed.");
        }

        List<PaymentEntity> payment = paymentRepository.findByUsername(username);
        for (var i : payment) {
            if(i.getCurrentState().equals(State.CURRENT)) {
                i.setBalance(i.getBalance().add(amount));
                break;
            }
        }

        paymentRepository.saveAll(payment);

        return "Balance updated successfully.";
    }

    public String withdraw(String username, BigDecimal amount) {
        if (!paymentRepository.existsByUsername(username)) {
            throw new InvalidAccountException("Verify and register a default account to proceed.");
        }

        List<PaymentEntity> paymentEntities = paymentRepository.findByUsername(username);
        PaymentEntity payment = null;
        for (var i : paymentEntities) {
            if (i.getCurrentState().equals(State.CURRENT)) {
                payment = i;
                break;
            }
        }

        if (payment == null) return "ISR";

        if (payment.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Balance is not sufficient for the request.");
        }

        payment.setBalance(payment.getBalance().subtract(amount));
        paymentRepository.save(payment);

        return "Withdraw successful.";
    }

    @Transactional
    public String transfer(String username, String toUser, BigDecimal amount) {
        if (!paymentRepository.existsByUsername(username)) {
            throw new InvalidAccountException("Verify and register a default account to proceed.");
        }

        if (!paymentRepository.existsByUsername(toUser)) {
            throw new InvalidAccountException("No user found with username " + toUser + ".");
        }

        List<PaymentEntity> fromUserEntities = paymentRepository.findByUsername(username);
        PaymentEntity fromUserEntity = null;
        List<PaymentEntity> toUserEntities = paymentRepository.findByUsername(toUser);
        PaymentEntity toUserEntity = null;

        for (var i : fromUserEntities) {
            if (i.getCurrentState().equals(State.CURRENT)) {
                fromUserEntity = i;
                break;
            }
        }

        for (var i : toUserEntities) {
            if (i.getCurrentState().equals(State.CURRENT)) {
                toUserEntity = i;
                break;
            }
        }

        if (fromUserEntity == null || toUserEntity == null) return "Internal server error. Try again";

        if (fromUserEntity.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Balance is not sufficient for the request.");
        }

        fromUserEntity.setBalance(fromUserEntity.getBalance().subtract(amount));
        paymentRepository.save(fromUserEntity);

        toUserEntity.setBalance(toUserEntity.getBalance().add(amount));
        paymentRepository.save(toUserEntity);

        return "Transfer successful";
    }
}
