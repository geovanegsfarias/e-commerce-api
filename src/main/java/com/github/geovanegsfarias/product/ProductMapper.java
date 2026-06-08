package com.github.geovanegsfarias.product;

import com.github.geovanegsfarias.category.CategoryMapper;

public class ProductMapper {

    public static Product toProduct(CreateProductRequest request) {
        return new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock()
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