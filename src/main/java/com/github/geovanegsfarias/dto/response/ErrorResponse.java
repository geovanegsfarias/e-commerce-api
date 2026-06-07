package com.github.geovanegsfarias.dto.response;

import java.time.Instant;

public record ErrorResponse(int status, Instant timestamp, String message, String path) {
}
