package com.vedavyaas.apigateway.user;

import com.vedavyaas.apigateway.records.JWTResponse;
import com.vedavyaas.apigateway.records.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class UserLoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public UserLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccount(LoginRequest loginRequest) {
        if (userRepository.existsByUsername(loginRequest.username())) return "Username already exists";

        UserEntity user = new UserEntity(loginRequest.username(), passwordEncoder.encode(loginRequest.password()));
        userRepository.save(user);

        return "User created";
    }

    ResponseEntity<JWTResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        String token = createToken(authentication);
        return ResponseEntity.ok(new JWTResponse(token));
    }

    private String createToken(Authentication authentication) {
        var claims = JwtClaimsSet.builder().issuer("self").issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60 * 10)).subject(authentication.getName()).claim("scope", createScope(authentication)).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createScope(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
    }
}