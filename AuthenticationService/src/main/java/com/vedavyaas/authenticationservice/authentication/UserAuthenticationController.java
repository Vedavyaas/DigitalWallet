package com.vedavyaas.authenticationservice.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthenticationController {
    private final UserAuthenticationService userAuthenticationService;

    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/api/user/create")
    public ResponseEntity<String> createAccount(@RequestBody CreateAccount createAccount) {
        return ResponseEntity.ok(userAuthenticationService.createAccount(createAccount));
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<JWTToken> login(@RequestBody RequestAuth requestAuth) {
        return ResponseEntity.ok(userAuthenticationService.login(requestAuth));
    }

    @ExceptionHandler(DuplicateCredentialException.class)
    public ResponseEntity<String> handle(DuplicateCredentialException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
