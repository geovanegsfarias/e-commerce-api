package com.github.geovanegsfarias.commons;

import com.github.geovanegsfarias.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductUtils {
    private final CategoryUtils categoryUtils;

    public ProductUtils(CategoryUtils categoryUtils) {
        this.categoryUtils = categoryUtils;
    }

    public Product newProductToSave() {
        var category = categoryUtils.savedCategory();

        return Product.builder()
                .name("Wireless headphones")
                .description("Noise-canceling wireless headphones")
                .price(new BigDecimal("299.90"))
                .stock(25)
                .category(category)
                .build();
    }

    public Product savedProduct() {
        var category = categoryUtils.savedCategory();

        return Product.builder()
                .id(1L)
                .name("Wireless headphones")
                .description("Noise-canceling wireless headphones")
                .price(new BigDecimal("299.90"))
                .stock(25)
                .category(category)
                .build();
    }

    public List<Product> newProductList() {
        var category = categoryUtils.savedCategory();

        var product = Product.builder()
                .id(1L)
                .name("Wireless headphones")
                .description("Noise-canceling wireless headphones")
                .price(new BigDecimal("299.90"))
                .stock(25)
                .category(category)
                .build();
        var product2 = Product.builder()
                .id(2L)
                .name("PlayStation 5 Slim Console")
                .description("PlayStation 5 Slim Console with 825 GB storage capacity")
                .price(new BigDecimal("449.99"))
                .stock(37)
                .category(category)
                .build();
        var product3 = Product.builder()
                .id(3L)
                    .name("Nugget Ice Maker")
                .description("Ultra Nugget Ice Maker with Side Tank and Scale Inhibiting Filter")
                .price(new BigDecimal("59.99"))
                .stock(50)
                .category(category)
                .build();

        return List.of(product, product2, product3);
    }
}
