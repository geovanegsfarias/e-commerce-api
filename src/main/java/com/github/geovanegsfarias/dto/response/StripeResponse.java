package com.github.geovanegsfarias.dto.response;

public record StripeResponse(String status, String message, String sessionId, String sessionUrl) {
}
