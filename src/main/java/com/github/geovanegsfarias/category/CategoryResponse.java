package com.github.geovanegsfarias.category;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Electronics") String name) {
}