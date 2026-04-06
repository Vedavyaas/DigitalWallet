package com.vedavyaas.loanservice.process;

import com.vedavyaas.loanservice.credit.LoanState;
import jakarta.persistence.*;

@Entity
public class LoanRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private double loanAmount;
    @Enumerated(EnumType.STRING)
    private LoanState loanProcessed;

    public LoanRequestEntity() {}

    public LoanRequestEntity(String username, double loanAmount) {
        this.username = username;
        this.loanAmount = loanAmount;
        this.loanProcessed = LoanState.WAITING;
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

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public LoanState getLoanProcessed() {
        return loanProcessed;
    }

    public void setLoanProcessed(LoanState loanProcessed) {
        this.loanProcessed = loanProcessed;
    }
}
