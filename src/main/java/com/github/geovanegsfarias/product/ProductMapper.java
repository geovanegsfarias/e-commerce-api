package com.github.geovanegsfarias.product;

import com.github.geovanegsfarias.category.Category;
import com.github.geovanegsfarias.category.CategoryMapper;

public class ProductMapper {

    public static Product toProduct(CreateProductRequest request, Category category) {
        return new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                category
        );
    }

    public static ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                CategoryMapper.toCategoryResponse(product.getCategory())
        );
    }

}