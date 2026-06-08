package com.github.geovanegsfarias.auth;

import com.github.geovanegsfarias.configuration.JwtConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public JwtService(JwtEncoder encoder, JwtConfigurationProperties jwtProperties) {
        this.encoder = encoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Authentication authentication) {
        var instant = Instant.now();
        var expiry = 3600L;

        var scopes = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("e-commerce-api")
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
