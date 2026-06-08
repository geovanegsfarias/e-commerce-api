package com.github.geovanegsfarias.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long userId,
        @Schema(example = "PENDING") OrderStatus status,
        @Schema(example = "2026-06-08T20:00:00Z") Instant createdAt,
        List<OrderItemResponse> orderItems,
        @Schema(type = "string", example = "599.80") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal totalPrice) {
}
