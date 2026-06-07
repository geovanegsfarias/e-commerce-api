package com.geovane.e_commerce_api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtConfigurationProperties(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey,
        String issuer,
        Duration expiration) {
}
