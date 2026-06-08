package com.github.geovanegsfarias.payment;

import io.swagger.v3.oas.annotations.media.Schema;

public record CheckoutResponse(
        @Schema(example = "SUCCESS") String status,
        @Schema(example = "Payment session created") String message,
        @Schema(example = "cs_test_gsf") String sessionId,
        @Schema(example = "https://checkout.stripe.com/c/pay/cs_test_gsf") String sessionUrl) {
}