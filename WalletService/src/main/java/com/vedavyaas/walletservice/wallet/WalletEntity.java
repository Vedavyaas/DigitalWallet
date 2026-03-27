package com.vedavyaas.walletservice.wallet;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private List<BankAccountEntity> bankAccounts = new ArrayList<>();
    private String primaryBankAccount;

    public WalletEntity() { }

    public WalletEntity(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BankAccountEntity> getBankAccounts() {
        return bankAccounts;
    }

    public void addBankAccountEntity(BankAccountEntity bankAccount) {
        this.bankAccounts.add(bankAccount);
    }

    public void addBankAccountEntities(List<BankAccountEntity> newBankAccounts) {
        this.bankAccounts.addAll(newBankAccounts);
    }

    public String getPrimaryBankAccount() {
        return primaryBankAccount;
    }

    public void setPrimaryBankAccount(String primaryBankAccount) {
        this.primaryBankAccount = primaryBankAccount;
    }

    public void setBankAccounts(List<BankAccountEntity> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
}