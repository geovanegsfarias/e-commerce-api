package com.github.geovanegsfarias.category;

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
