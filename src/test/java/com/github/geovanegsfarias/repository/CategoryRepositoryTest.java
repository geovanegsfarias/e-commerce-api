package com.github.geovanegsfarias.repository;

import com.github.geovanegsfarias.category.Category;
import com.github.geovanegsfarias.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldReturnTrueWhenNameExistsIgnoringCase() {
        categoryRepository.save(new Category("TV"));

        boolean exists = categoryRepository.existsByNameIgnoreCase("tv");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNameNotExistsIgnoringCase() {
        boolean exists = categoryRepository.existsByNameIgnoreCase("Food");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenNameAlreadyExistsWithDifferentId() {
        Category category = categoryRepository.save(new Category("Games"));

        boolean exists = categoryRepository.existsByNameIgnoreCaseAndIdNot("Games", category.getId() + 1);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNameNotExistsWithDifferentId() {
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndIdNot("Books", 5L);

        assertThat(exists).isFalse();
    }

}
