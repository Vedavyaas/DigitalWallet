package com.vedavyaas.walletservice.verify;

import jakarta.persistence.*;

@Entity
public class VerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String accountNumber;
    @Enumerated(value = EnumType.STRING)
    private VerificationStatus verificationStatus;

    public VerificationEntity() { }

    public VerificationEntity(String username, String accountNumber) {
        this.username = username;
        this.accountNumber = accountNumber;
        this.verificationStatus = VerificationStatus.NOT_PROCESSED;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
