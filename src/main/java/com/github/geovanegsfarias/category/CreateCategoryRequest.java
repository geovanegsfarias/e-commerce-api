package com.github.geovanegsfarias.category;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "The category name must not be blank.") String name) {
}
