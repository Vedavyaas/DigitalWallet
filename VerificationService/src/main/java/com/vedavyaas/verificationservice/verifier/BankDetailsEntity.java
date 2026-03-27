package com.vedavyaas.verificationservice.verifier;

import jakarta.persistence.*;

@Entity
public class BankDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String accountNumber;
    private String bankUsername;
    private String CIFNumber;
    private String branch;
    private String IFSCCode;
    private String bankName;
    private String accountType;
    private String userEmailOnBank;
    @Enumerated(value = EnumType.STRING)
    private VerificationStatus verified;

    public BankDetailsEntity() { }

    public BankDetailsEntity(String username, String email, String accountNumber, String bankUsername, String CIFNumber, String branch, String IFSCCode, String bankName, String accountType, String userEmailOnBank) {
        this.username = username;
        this.email = email;
        this.accountNumber = accountNumber;
        this.bankUsername = bankUsername;
        this.CIFNumber = CIFNumber;
        this.branch = branch;
        this.IFSCCode = IFSCCode;
        this.bankName = bankName;
        this.accountType = accountType;
        this.userEmailOnBank = userEmailOnBank;
        this.verified = VerificationStatus.PENDING;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankUsername() {
        return bankUsername;
    }

    public void setBankUsername(String bankUsername) {
        this.bankUsername = bankUsername;
    }

    public String getCIFNumber() {
        return CIFNumber;
    }

    public void setCIFNumber(String CIFNumber) {
        this.CIFNumber = CIFNumber;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getIFSCCode() {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode) {
        this.IFSCCode = IFSCCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserEmailOnBank() {
        return userEmailOnBank;
    }

    public void setUserEmailOnBank(String userEmailOnBank) {
        this.userEmailOnBank = userEmailOnBank;
    }

    public VerificationStatus getVerified() {
        return verified;
    }

    public void setVerified(VerificationStatus verified) {
        this.verified = verified;
    }
}
