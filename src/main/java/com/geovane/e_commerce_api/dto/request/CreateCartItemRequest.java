package com.geovane.e_commerce_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateCartItemRequest(
        @NotNull(message = "The product id must not be null.") Long productId,
        @Min(value = 1, message = "Add at least one unit of this product.") int quantity) {
}