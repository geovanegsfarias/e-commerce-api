package com.geovane.e_commerce_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "The category name must not be blank.") String name) {
}
