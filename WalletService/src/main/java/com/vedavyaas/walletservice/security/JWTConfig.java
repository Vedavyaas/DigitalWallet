package com.vedavyaas.walletservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class JWTConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    RSAPublicKey publicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuC/9x0iyQ9hFhtVvsu0bDkp8Jp6+PtxkJv1/V3k8nI+fAQfSZp5u4VcVlgD4mTzlt4+1KjozC5IbC2rnO6c9+1K3f6l2JvLUKU6BQt9rHqYt0Qx3d1c7m0nT0u8+Lqv8edEYq4Vd0B6vlL2gTZn1KzS9zQ3sUbQ2xF+FZ5v9Ox5/rLfSW+6fV3+7HtB+XpZzy0NMLyLqOx9tD0xdpvDd3xn7lA7SZ7+8P3uU5Fow3hUO+m1PsWZ6YyfI07cHi4DJNpuJ5mD7RjPMN+KTTlPCEeD7sLweZUZH0B1Ljz/yaXGp5d3NaZpRzAxkM9H0jZvD/k5FqfZCK+bfM0tT6jK+0wIDAQAB";
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }

    @Bean
    JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
