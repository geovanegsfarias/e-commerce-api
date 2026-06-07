package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.request.CreateCategoryRequest;
import com.github.geovanegsfarias.dto.response.CategoryResponse;
import com.github.geovanegsfarias.model.Category;

public class CategoryMapper {

    public static Category toCategory(CreateCategoryRequest request) {
        return new Category(
                request.name()
        );
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

}
