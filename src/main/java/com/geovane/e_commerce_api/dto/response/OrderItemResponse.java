package com.geovane.e_commerce_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId, String productName, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal price, int quantity) {
}