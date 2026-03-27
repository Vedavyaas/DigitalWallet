package com.vedavyaas.walletservice.wallet;

import com.vedavyaas.walletservice.bank.BankAccountRegistration;
import jakarta.persistence.*;

@Entity
@Table(indexes = {
    @Index(name = "idx_bankaccount_status", columnList = "userUpdateRequest, updated, verified")
})
public class BankAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String accountNumber;
    private String CIFNumber;
    private String branch;
    private String IFSCCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Bank bankName;
    private String accountType;
    private String emailBank;
    private boolean verified;
    private boolean updated;
    private boolean userUpdateRequest;

    public BankAccountEntity() { }

    public BankAccountEntity(BankAccountRegistration bankAccountRegistration) {
        this.name = bankAccountRegistration.name();
        this.accountNumber = bankAccountRegistration.accountNumber();
        this.CIFNumber = bankAccountRegistration.CIFNumber();
        this.branch = bankAccountRegistration.branch();
        this.IFSCCode = bankAccountRegistration.IFSCCode();
        this.bankName = bankAccountRegistration.bankName();
        this.accountType = bankAccountRegistration.accountType();
        this.emailBank = bankAccountRegistration.emailBank();
        this.verified = false;
        this.updated = false;
        this.userUpdateRequest = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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

    public Bank getBankName() {
        return bankName;
    }

    public void setBankName(Bank bankName) {
        this.bankName = bankName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getEmailBank() {
        return emailBank;
    }

    public void setEmailBank(String emailFromBank) {
        this.emailBank = emailFromBank;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isUserUpdateRequest() {
        return userUpdateRequest;
    }

    public void setUserUpdateRequest(boolean userUpdateRequest) {
        this.userUpdateRequest = userUpdateRequest;
    }
}