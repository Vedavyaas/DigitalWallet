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

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Configuration
@EnableWebFluxSecurity
public class JWTConfig {

    private static volatile KeyPair fallbackKeyPair;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.authorizeExchange(exchange -> exchange
                .pathMatchers("/eureka/**").permitAll()
                .anyExchange().authenticated());
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    RSAPrivateKey privateKey() throws Exception {
        String key = "MIIEpAIBAAKCAQEAuC/9x0iyQ9hFhtVvsu0bDkp8Jp6+PtxkJv1/V3k8nI+fAQfSZp5u4VcVlgD4mTzlt4+1KjozC5IbC2rnO6c9+1K3f6l2JvLUKU6BQt9rHqYt0Qx3d1c7m0nT0u8+Lqv8edEYq4Vd0B6vlL2gTZn1KzS9zQ3sUbQ2xF+FZ5v9Ox5/rLfSW+6fV3+7HtB+XpZzy0NMLyLqOx9tD0xdpvDd3xn7lA7SZ7+8P3uU5Fow3hUO+m1PsWZ6YyfI07cHi4DJNpuJ5mD7RjPMN+KTTlPCEeD7sLweZUZH0B1Ljz/yaXGp5d3NaZpRzAxkM9H0jZvD/k5FqfZCK+bfM0tT6jK+0wIDAQABAoIBAQCv4R+Zxk5Twc0Qb8K/3X7td2X/FBMFm+0zLfqZ+N6zPz9GQ/1z0wJpZ0l+2pH66d2Kw8i/QC3m2sDlkTnWhlh0Zj7h8q1n8p7LJqdfcT0Z0vA9kpZpHqGcYx0QZsQZ8pKycRvH9HtR6Dk9ZcG+hD7D3D7Vz0z8rjJqMb8uK3l5TX7H3r9R6iZ/6Wfn3Yz4g6QY4cN0QZlFjBZd7AqGmQf+1UgGr8H9xDftN5o+gXjT0rjC3JYdCjG3uGkB/jG54iJz+6U5XrUoVv7nj1+R8kOlA0MC+jClbqZXg7ONsf2H0e1bX/ZXjUymOHy3gdyNq7mcL2ZkVhDE9h5M4B+lAoGBAPYhG+Gk8P+QiKHsDqS2xI0k2lKmnZQx6HxM6XkgVoF+P2X7Uo4lXx+78M3r3F5BhZp0O5l/8MGZcW9iU8TzfjG6e0W1BnxZmG8pM9gQi+1ZdDmjJFiOqJp0m5iYl2PjxT+X8Vz6qxk9Zsnq4oFblbJc5vSyS5CjI2x+37A3gBkLAoGBAMxqztWd1R/XKCDqk3v0M/7VwB0Q4LKdxFXuT7F7lpxV3J3uJtGvCTbK7xO3F9k4V+Z+FZhRLw+Q0i/uJzLxFZ8+qWz8o3mE3i1vnLyjPpQhT5hZk9uUu5R6NZ/r95QZ3sTgfI2C4kCEq4U3Pyc4XjFEmFnhbQe5QqLpr0r5oZVfAoGABVqGhs0tckHnI3dpkFZ3nTJ6GvZlqB/0N5NQq0o2F9q3N5d3MJkIBiQmER0iM1yUM0KkKXtG9m0uTj0d1YIz7M4R9G0lHeKfXc8pm5u5R3hvxXhU0l+K3cJ3r8Gh+JZ+Np8lJpK0Zb/9iZAmVFeIUGR1I+XsfQhpkfYv9GChcT0CgYEAtnP2xO4sVf8M3R6i8/5P4Nln9fHgP1g5+e2lZ6k2NqvGjF/3rZf6uXkJr/E4OlM6Gj1qFZtUVyFHyN2L0UVfR2fuw0X2kLgMZsOqQ0+NT1UoR7GLjZxG+u1KFlmVw6yTs0TTx1oFv+vXKvG2q7t7n1YNH13dF+h4V5QdFspRZm0=";
        byte[] decoded = Base64.getDecoder().decode(key);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (InvalidKeySpecException ex) {
            try {
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(wrapPkcs1RsaPrivateKeyToPkcs8(decoded));
                return (RSAPrivateKey) kf.generatePrivate(spec);
            } catch (Exception inner) {
                return (RSAPrivateKey) getOrCreateFallbackKeyPair().getPrivate();
            }
        } catch (Exception ex) {
            return (RSAPrivateKey) getOrCreateFallbackKeyPair().getPrivate();
        }
    }

    private static byte[] wrapPkcs1RsaPrivateKeyToPkcs8(byte[] pkcs1Der) {
        try {
            ByteArrayOutputStream inner = new ByteArrayOutputStream();
            inner.write(0x02);
            inner.write(0x01);
            inner.write(0x00);

            inner.write(new byte[] {
                    0x30, 0x0D,
                    0x06, 0x09,
                    0x2A, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xF7, 0x0D, 0x01, 0x01, 0x01,
                    0x05, 0x00
            });
            
            inner.write(0x04);
            inner.write(encodeDerLength(pkcs1Der.length));
            inner.write(pkcs1Der);

            byte[] innerBytes = inner.toByteArray();
            ByteArrayOutputStream outer = new ByteArrayOutputStream();
            outer.write(0x30);
            outer.write(encodeDerLength(innerBytes.length));
            outer.write(innerBytes);
            return outer.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to wrap PKCS#1 key into PKCS#8", e);
        }
    }

    private static byte[] encodeDerLength(int length) {
        if (length < 0x80) {
            return new byte[] { (byte) length };
        }
        int tmp = length;
        int numBytes = 0;
        while (tmp > 0) {
            tmp >>= 8;
            numBytes++;
        }
        byte[] out = new byte[1 + numBytes];
        out[0] = (byte) (0x80 | numBytes);
        for (int i = numBytes; i > 0; i--) {
            out[i] = (byte) (length & 0xFF);
            length >>= 8;
        }
        return out;
    }

    @Bean
    RSAPublicKey publicKey() throws Exception {
        KeyPair kp = fallbackKeyPair;
        if (kp != null) {
            return (RSAPublicKey) kp.getPublic();
        }
        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuC/9x0iyQ9hFhtVvsu0bDkp8Jp6+PtxkJv1/V3k8nI+fAQfSZp5u4VcVlgD4mTzlt4+1KjozC5IbC2rnO6c9+1K3f6l2JvLUKU6BQt9rHqYt0Qx3d1c7m0nT0u8+Lqv8edEYq4Vd0B6vlL2gTZn1KzS9zQ3sUbQ2xF+FZ5v9Ox5/rLfSW+6fV3+7HtB+XpZzy0NMLyLqOx9tD0xdpvDd3xn7lA7SZ7+8P3uU5Fow3hUO+m1PsWZ6YyfI07cHi4DJNpuJ5mD7RjPMN+KTTlPCEeD7sLweZUZH0B1Ljz/yaXGp5d3NaZpRzAxkM9H0jZvD/k5FqfZCK+bfM0tT6jK+0wIDAQAB";
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
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
