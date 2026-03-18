package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.response.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldReturnGeneratedToken() {
        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(jwtService.generateToken(authentication)).thenReturn("akgprwgplrg");

        LoginResponse returnedAuth = authService.authenticate(authentication);

        assertThat(returnedAuth.token()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationIsNull() {
        assertThatThrownBy(() -> authService.authenticate(null)).isInstanceOf(BadCredentialsException.class);
    }

}