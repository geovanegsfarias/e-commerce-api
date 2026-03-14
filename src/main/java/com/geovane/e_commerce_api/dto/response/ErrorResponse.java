package com.geovane.e_commerce_api.dto.response;

import java.time.Instant;

public record ErrorResponse(int status, Instant timestamp, String message, String path) {
}
