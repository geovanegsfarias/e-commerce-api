package com.geovane.e_commerce_api.repository;

import com.geovane.e_commerce_api.model.Category;
import com.geovane.e_commerce_api.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;


@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldReturnTrueWhenCategoryExistsById() {
        Product product = productRepository.save(
                new Product("Xbox One", "Xbox One description.", BigDecimal.TEN, 1, new Category("Console")));

        boolean exists = productRepository.existsByCategoryId(product.getCategory().getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCategoryNotExistsById() {
        boolean exists = productRepository.existsByCategoryId(50L);

        assertThat(exists).isFalse();
    }

}