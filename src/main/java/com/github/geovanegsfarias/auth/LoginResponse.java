package com.github.geovanegsfarias.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(example = "eyJhbOiJIUzsC...") String token) {
}
