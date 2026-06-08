package com.github.geovanegsfarias.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required") String name,
        @NotBlank(message = "Product description is required") String description,
        @NotNull(message = "Product price is required") @DecimalMin(value = "0.01", message = "Price must be at least 0.01") BigDecimal price,
        @PositiveOrZero(message = "The stock cannot be negative") int stock,
        @NotNull(message = "Category ID is required") Long categoryId) {
}