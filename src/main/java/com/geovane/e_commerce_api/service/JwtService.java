package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.configuration.JwtConfigurationProperties;
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
    private final JwtConfigurationProperties jwtProperties;

    public JwtService(JwtEncoder encoder, JwtConfigurationProperties jwtProperties) {
        this.encoder = encoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scopes = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.expiration()))
                .subject(authentication.getName())
                .claim("scope", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
