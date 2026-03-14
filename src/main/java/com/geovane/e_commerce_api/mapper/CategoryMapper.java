package com.geovane.e_commerce_api.mapper;

import com.geovane.e_commerce_api.dto.request.CreateCategoryRequest;
import com.geovane.e_commerce_api.dto.response.CategoryResponse;
import com.geovane.e_commerce_api.model.Category;

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
