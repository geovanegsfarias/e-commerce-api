package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtService jwtService;

    @Autowired
    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public LoginResponse authenticate(Authentication authentication) {
        if (authentication == null) {
            throw new BadCredentialsException("Invalid username or password.");
        }
        return new LoginResponse(jwtService.generateToken(authentication));
    }

}