package com.github.geovanegsfarias.order;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, Long userId, OrderStatus status, Instant createdAt,
                            List<OrderItemResponse> orderItems,
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal totalPrice) {
}