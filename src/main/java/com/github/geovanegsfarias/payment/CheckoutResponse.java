package com.github.geovanegsfarias.payment;

public record CheckoutResponse(String status, String message, String sessionId, String sessionUrl) {
}
