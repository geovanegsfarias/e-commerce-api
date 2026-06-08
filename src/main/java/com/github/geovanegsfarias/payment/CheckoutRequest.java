package com.github.geovanegsfarias.payment;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(@NotNull(message = "Order ID is required") Long orderId) {
}