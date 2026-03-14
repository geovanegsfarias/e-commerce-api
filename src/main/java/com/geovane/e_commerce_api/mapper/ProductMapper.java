package com.geovane.e_commerce_api.mapper;

import com.geovane.e_commerce_api.dto.request.CreateProductRequest;
import com.geovane.e_commerce_api.dto.response.ProductResponse;
import com.geovane.e_commerce_api.model.Category;
import com.geovane.e_commerce_api.model.Product;

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