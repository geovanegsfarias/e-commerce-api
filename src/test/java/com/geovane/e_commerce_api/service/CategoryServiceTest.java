package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.request.CreateCategoryRequest;
import com.geovane.e_commerce_api.dto.response.CategoryResponse;
import com.geovane.e_commerce_api.exception.ResourceAlreadyExistsException;
import com.geovane.e_commerce_api.exception.ResourceInUseException;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.model.Category;
import com.geovane.e_commerce_api.repository.CategoryRepository;
import com.geovane.e_commerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldReturnAllCategories() {
        List<Category> categoriesList = List.of(new Category("Category A"), new Category("Category B"), new Category("Category C"));

        Mockito.when(categoryRepository.findAll()).thenReturn(categoriesList);

        List<CategoryResponse> categories = categoryService.getAll();

        assertThat(categories).hasSize(categoriesList.size());
    }

    @Test
    void shouldReturnCategoryById() {
        Category category = new Category("Category");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse returnedCategory = categoryService.getById(1L);

        assertThat(returnedCategory.name()).isEqualTo(category.getName());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnSavedCategory() {
        Category category = new Category("Category");

        Mockito.when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse returnedCategory = categoryService.save(new CreateCategoryRequest("Category"));

        assertThat(returnedCategory.name()).isEqualTo(category.getName());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNameAlreadyInUse() {
        CreateCategoryRequest request = new CreateCategoryRequest("Category");

        Mockito.when(categoryRepository.existsByNameIgnoreCase(request.name())).thenReturn(true);

        assertThatThrownBy(() -> categoryService.save(request)).isInstanceOf(ResourceAlreadyExistsException.class);
    }


    @Test
    void shouldReturnUpdatedCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("Updated Category");
        Category category = new Category("Category");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(any(Category.class))).thenReturn(new Category("Updated category"));

        CategoryResponse returnedCategory = categoryService.update(request, 1L);

        assertThat(returnedCategory.name()).isEqualTo("Updated category");
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnUpdate() {
        CreateCategoryRequest request = new CreateCategoryRequest("Updated Category");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(request, 1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNameAlreadyInUseOnUpdate() {
        CreateCategoryRequest request = new CreateCategoryRequest("Updated Category");
        Category category = new Category("Category");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.existsByNameIgnoreCaseAndIdNot(request.name(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(request, 1L)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void shouldDeleteCategory() {
        Category category = new Category("Category");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(productRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.delete(1L);

        Mockito.verify(categoryRepository).delete(category);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnDelete() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNameIsInUse() {
        Category category = new Category("Category");

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(1L)).isInstanceOf(ResourceInUseException.class);
    }

}