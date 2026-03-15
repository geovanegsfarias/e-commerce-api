package com.geovane.e_commerce_api.dto.request;

import jakarta.validation.constraints.NotNull;

public record StripeRequest(@NotNull(message = "The order id must not be null.") Long orderId) {
}
