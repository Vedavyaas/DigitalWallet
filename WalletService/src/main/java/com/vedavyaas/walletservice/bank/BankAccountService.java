package com.vedavyaas.walletservice.bank;

import com.vedavyaas.walletservice.wallet.BankAccountEntity;
import com.vedavyaas.walletservice.wallet.BankAccountRepository;
import com.vedavyaas.walletservice.wallet.WalletEntity;
import com.vedavyaas.walletservice.wallet.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final WalletRepository walletRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository, WalletRepository walletRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public String registerBankAccount(String username, BankAccountRegistration bankAccountRegistration) {
        WalletEntity wallet = walletRepository.findByUsername(username);

        if (bankAccountRepository.existsByAccountNumber(bankAccountRegistration.accountNumber())) {
            throw new DuplicateCredentialsException("Bank account with " +
                    "this account number already exists.");
        }

        if (!bankAccountRegistration.emailBank().equals(wallet.getEmail())) {
            throw new InvalidCredentialsException("Invalid email, email must match.");
        }

        BankAccountEntity bankAccountEntity = new BankAccountEntity(bankAccountRegistration);
        wallet.addBankAccountEntity(bankAccountEntity);

        bankAccountRepository.save(bankAccountEntity);
        walletRepository.save(wallet);

        return "Account added successfully.";
    }

    public List<BankAccountEntity> findAllBankAccountsRegistered(String username) {
        WalletEntity wallet = walletRepository.findByUsername(username);
        if (wallet == null || wallet.getBankAccounts() == null) return null;
        return wallet.getBankAccounts();
    }

    public String registerDefaultBankAccount(Long bankId, String username) {
        WalletEntity wallet = walletRepository.findByUsername(username);
        List<BankAccountEntity> bankAccountEntities = wallet.getBankAccounts();

        for (var i : bankAccountEntities) {
            if (Objects.equals(i.getId(), bankId)) {
                if(i.isVerified()) {
                    wallet.setPrimaryBankAccount(i.getAccountNumber());
                    walletRepository.save(wallet);
                    break;
                }
                else throw new VerificationPendingException("Bank account verification pending.");
            }
        }

        return "Bank account added as default";
    }

    public String verifyBankAccount(Long bankId, String username) {
        WalletEntity wallet = walletRepository.findByUsername(username);
        List<BankAccountEntity> bankAccountEntities = wallet.getBankAccounts();

        for (var i : bankAccountEntities) {
            if (Objects.equals(bankId, i.getId())) {
                if (!i.isVerified()) {
                    if (!i.isUpdated()) {
                        i.setUserUpdateRequest(true);
                        i.setUpdated(false);
                        bankAccountRepository.save(i);
                        return "Bank account verification in-progress";
                    }
                    else throw new VerificationPendingException("Bank account verification is in-progress");
                }
                else throw new InvalidCredentialsException("Bank account is already verified.");
            }
        }

        throw new InvalidCredentialsException("Not valid bank id.");
    }
}