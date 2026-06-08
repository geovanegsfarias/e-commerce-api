package com.github.geovanegsfarias.exception;

import java.time.Instant;

public record ErrorResponse(
        int status,
        Instant timestamp,
        String message,
        String path) {
}
