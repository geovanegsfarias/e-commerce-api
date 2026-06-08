package com.github.geovanegsfarias.auth;

import com.github.geovanegsfarias.configuration.JwtConfigurationProperties;
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
        var instant = Instant.now();

        var scopes = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(instant)
                .expiresAt(instant.plus(jwtProperties.expiration()))
                .subject(authentication.getName())
                .claim("scope", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
