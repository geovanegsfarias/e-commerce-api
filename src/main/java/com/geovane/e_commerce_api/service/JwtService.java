package com.geovane.e_commerce_api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 3600L;

        String scopes = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("e-commerce-api")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
