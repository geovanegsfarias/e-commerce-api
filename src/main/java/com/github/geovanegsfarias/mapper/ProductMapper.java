package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.request.CreateProductRequest;
import com.github.geovanegsfarias.dto.response.ProductResponse;
import com.github.geovanegsfarias.model.Category;
import com.github.geovanegsfarias.model.Product;

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