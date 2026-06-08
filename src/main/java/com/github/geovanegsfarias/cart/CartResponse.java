package com.github.geovanegsfarias.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long userId,
        List<CartItemResponse> items,
        @Schema(type = "string", example = "599.80") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal totalPrice) {
}
