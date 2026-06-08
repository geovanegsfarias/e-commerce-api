package com.github.geovanegsfarias.auth;

import com.github.geovanegsfarias.user.CreateUserRequest;
import com.github.geovanegsfarias.user.UserMapper;
import com.github.geovanegsfarias.user.UserResponse;
import com.github.geovanegsfarias.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper mapper;

    @Autowired
    public AuthController(AuthService authService, UserService userService, UserMapper mapper) {
        this.authService = authService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account.", security = {})
    @ApiResponse(responseCode = "201", description = "User registered successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request data.")
    @ApiResponse(responseCode = "409", description = "Email already registered.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid CreateUserRequest request) {
        log.debug("Request received to register a user");

        var userToSave = mapper.toUser(request);

        var savedUser = userService.save(userToSave);

        var userResponse = mapper.toUserResponse(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns a JWT token for authorized requests.", security = @SecurityRequirement(name = "Basic Auth"))
    @ApiResponse(responseCode = "200", description = "Successfully authenticated.")
    @ApiResponse(responseCode = "401", description = "Invalid username or password.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<LoginResponse> login(Authentication authentication) {
        log.debug("Request received to authenticate user");

        var token = authService.authenticate(authentication);

        var loginResponse = new LoginResponse(token);

        return ResponseEntity.ok(loginResponse);
    }

}