package com.vedavyaas.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@EnableWebFluxSecurity
public class JWTConfig {

    private static volatile KeyPair fallbackKeyPair;

    @Bean
    KeyPair rsaKeyPair() {
        return getOrCreateFallbackKeyPair();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.authorizeExchange(exchange -> exchange
                .pathMatchers("/eureka/**","/create/account/**","/login/account/**","/hello").permitAll()
                .anyExchange().authenticated());
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    RSAPrivateKey privateKey(KeyPair rsaKeyPair) {
        return (RSAPrivateKey) rsaKeyPair.getPrivate();
    }

    @Bean
    RSAPublicKey publicKey(KeyPair rsaKeyPair) {
        return (RSAPublicKey) rsaKeyPair.getPublic();
    }

    private static KeyPair getOrCreateFallbackKeyPair() {
        KeyPair kp = fallbackKeyPair;
        if (kp != null) {
            return kp;
        }
        synchronized (JWTConfig.class) {
            if (fallbackKeyPair == null) {
                try {
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                    generator.initialize(2048);
                    fallbackKeyPair = generator.generateKeyPair();
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to generate fallback RSA keypair", e);
                }
            }
            return fallbackKeyPair;
        }
    }

    @Bean
    JwtEncoder jwtEncoder(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        var rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        var jwkSet = new JWKSet(rsaKey);
        var jwkSource = new ImmutableJWKSet<>(jwkSet);
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsServiceImpl userDetailsService,
                                                PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
