package com.github.geovanegsfarias.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.geovanegsfarias.category.CategoryResponse;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description,
                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal price,
                              int stock, CategoryResponse category) {
}