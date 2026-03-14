package com.geovane.e_commerce_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "The product name must not be blank.") String name,
        @NotBlank(message = "The product description must not be blank.") String description,
        @NotNull(message = "The price must not be null.") @DecimalMin(value = "0.01", message = "The price must have a positive value.") BigDecimal price,
        @PositiveOrZero(message = "The stock cannot be negative.") int stock,
        @NotNull(message = "The category id must not be null.") Long categoryId) {
}