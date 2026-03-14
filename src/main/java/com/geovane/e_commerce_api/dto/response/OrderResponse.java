package com.geovane.e_commerce_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, Long userId, Instant createdAt, List<OrderItemResponse> orderItems, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal totalPrice) {
}