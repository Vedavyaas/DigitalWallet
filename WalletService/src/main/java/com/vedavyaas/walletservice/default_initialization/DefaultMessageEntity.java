package com.vedavyaas.walletservice.default_initialization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DefaultMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String defaultAccountNumber;
    private boolean updated;

    public DefaultMessageEntity() {
    }

    public DefaultMessageEntity(String username, String defaultAccountNumber) {
        this.username = username;
        this.defaultAccountNumber = defaultAccountNumber;
        this.updated = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getDefaultAccountNumber() {
        return defaultAccountNumber;
    }

    public void setDefaultAccountNumber(String defaultAccountNumber) {
        this.defaultAccountNumber = defaultAccountNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
