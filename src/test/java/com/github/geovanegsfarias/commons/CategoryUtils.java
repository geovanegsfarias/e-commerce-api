package com.github.geovanegsfarias.commons;

import com.github.geovanegsfarias.category.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryUtils {

    public Category newCategoryToSave() {
        return Category.builder()
                .name("Electronics")
                .build();
    }

    public Category savedCategory() {
        return Category.builder()
                .id(1L)
                .name("Electronics")
                .build();
    }

    public List<Category> newCategoryList() {
        var category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();
        var category2 = Category.builder()
                .id(2L)
                .name("Automotive")
                .build();
        var category3 = Category.builder()
                .id(3L)
                .name("Sports")
                .build();

        return List.of(category, category2, category3);
    }
}