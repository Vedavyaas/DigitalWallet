package com.vedavyaas.authenticationservice.authentication;

import com.vedavyaas.authenticationservice.user.Role;

public record CreateAccount(String username, String email, String password, Role role) {
}
