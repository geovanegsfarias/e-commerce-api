package com.github.geovanegsfarias.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.geovanegsfarias.category.CategoryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ProductResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Wireless headphones") String name,
        @Schema(example = "Noise-canceling wireless headphones") String description,
        @Schema(type = "string", example = "299.90")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal price,
        @Schema(example = "25") int stock,
        CategoryResponse category) {
}
