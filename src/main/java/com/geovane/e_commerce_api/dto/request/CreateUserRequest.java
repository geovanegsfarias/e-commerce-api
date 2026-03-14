package com.geovane.e_commerce_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "The username must not be blank.") String username,
        @NotBlank(message = "The email must not be blank.") @Email(message = "The email must be valid.") String email,
        @NotBlank(message = "The password must not be blank.") @Size(min=8, max=100, message = "Your password must be 8 characters or longer.") String password) {
}
