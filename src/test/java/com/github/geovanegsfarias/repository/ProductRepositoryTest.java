package com.github.geovanegsfarias.repository;

import com.github.geovanegsfarias.category.Category;
import com.github.geovanegsfarias.category.CategoryRepository;
import com.github.geovanegsfarias.product.Product;
import com.github.geovanegsfarias.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldReturnTrueWhenCategoryExistsById() {
        Category category = new Category("Console");
        categoryRepository.save(category);
        Product product = new Product("Xbox One", "Xbox One description.", BigDecimal.TEN, 1, category);
        productRepository.save(product);

        boolean exists = productRepository.existsByCategoryId(product.getCategory().getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCategoryNotExistsById() {
        boolean exists = productRepository.existsByCategoryId(50L);

        assertThat(exists).isFalse();
    }

}