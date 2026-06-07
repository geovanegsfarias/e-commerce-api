package com.github.geovanegsfarias.payment;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(@NotNull(message = "The order id must not be null.") Long orderId) {
}
