package com.geovane.e_commerce_api.dto.response;

public record StripeResponse(String status, String message, String sessionId, String sessionUrl) {
}
