package com.vedavyaas.authenticationservice.user;

import com.vedavyaas.authenticationservice.authentication.CreateAccount;
import jakarta.persistence.*;

@Entity
public class UserDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private boolean updated;

    public UserDetailsEntity() { }

    public UserDetailsEntity(CreateAccount createAccount) {
        this.username = createAccount.username();
        this.email = createAccount.email();
        this.role = createAccount.role();
        this.updated = false;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
