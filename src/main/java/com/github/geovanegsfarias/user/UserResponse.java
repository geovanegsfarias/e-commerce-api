package com.github.geovanegsfarias.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "user") String username,
        @Schema(example = "user@gmail.com") String email) {
}