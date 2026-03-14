package com.geovane.e_commerce_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00") BigDecimal price, int stock, CategoryResponse category) {
}