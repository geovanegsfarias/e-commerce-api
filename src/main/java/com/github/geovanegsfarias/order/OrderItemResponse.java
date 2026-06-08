package com.github.geovanegsfarias.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderItemResponse(
        @Schema(example = "10") Long productId,
        @Schema(example = "Wireless headphones") String productName,
        @Schema(type = "string", example = "299.90") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal price,
        @Schema(example = "2") int quantity) {
}
