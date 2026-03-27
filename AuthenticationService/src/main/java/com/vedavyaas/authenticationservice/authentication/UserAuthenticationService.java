package com.vedavyaas.authenticationservice.authentication;

import com.vedavyaas.authenticationservice.user.UserDetailsEntity;
import com.vedavyaas.authenticationservice.user.UserDetailsRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class UserAuthenticationService {
    private final UserDetailsRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public UserAuthenticationService(UserDetailsRepository userDetailsRepository, PasswordEncoder passwordEncoder,
                                     AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.userDetailsRepository = userDetailsRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccount(CreateAccount createAccount) {
        if(userDetailsRepository.existsByEmail(createAccount.email())) {
            throw new DuplicateCredentialException("Email already exist.");
        }

        if (userDetailsRepository.existsByUsername(createAccount.username())) {
            throw new DuplicateCredentialException("Username already exist.");
        }

        UserDetailsEntity userDetailsEntity = new UserDetailsEntity(createAccount);
        userDetailsEntity.setPassword(passwordEncoder.encode(createAccount.password()));
        userDetailsRepository.save(userDetailsEntity);

        return "Account created successfully";
    }

    public JWTToken login(RequestAuth requestAuth) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestAuth.username(), requestAuth.password())
        );

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(36000))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new JWTToken(token);
    }
}
