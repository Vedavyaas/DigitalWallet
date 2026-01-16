package com.vedavyaas.walletservice.repository;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private WalletEntity user;
    private String action;
    private LocalDateTime timestamp;

    public HistoryEntity() {
    }

    public HistoryEntity(WalletEntity user, String action) {
        this.user = user;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WalletEntity getUser() {
        return user;
    }

    public void setUser(WalletEntity user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
