package com.github.geovanegsfarias.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.geovanegsfarias.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, Long userId, OrderStatus status, Instant createdAt,
                            List<OrderItemResponse> orderItems,
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal totalPrice) {
}