package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.request.CreateUserRequest;
import com.geovane.e_commerce_api.dto.response.LoginResponse;
import com.geovane.e_commerce_api.dto.response.UserResponse;
import com.geovane.e_commerce_api.service.AuthService;
import com.geovane.e_commerce_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(Authentication authentication) {
        return ResponseEntity.ok(authService.authenticate(authentication));
    }

}