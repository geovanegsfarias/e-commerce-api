package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.request.CreateProductRequest;
import com.geovane.e_commerce_api.dto.response.ProductResponse;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.model.Category;
import com.geovane.e_commerce_api.model.Product;
import com.geovane.e_commerce_api.repository.CategoryRepository;
import com.geovane.e_commerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnAllProducts() {
        Category category = new Category("Category");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(10L), 1, category);
        Page<Product> products = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findAll(Pageable.unpaged())).thenReturn(products);

        Page<ProductResponse> returnedProducts = productService.getAll(Pageable.unpaged());

        assertThat(returnedProducts.getContent()).hasSize(products.getContent().size());
    }

    @Test
    void shouldReturnProductById() {
        Category category = new Category("Category");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(10L), 1, category);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse returnedProduct = productService.getById(1L);

        assertThat(returnedProduct.name()).isEqualTo(product.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundOnProductGet() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnSavedProduct() {
        CreateProductRequest request = new CreateProductRequest("Product", "Product description", BigDecimal.valueOf(100), 1, 1L);
        Category category = new Category("Category");
        Product product = new Product("Product", "Product description", BigDecimal.valueOf(100), 1, category);

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse returnedProduct = productService.save(request);

        assertThat(returnedProduct.name()).isEqualTo(request.name());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnProductPost() {
        CreateProductRequest request = new CreateProductRequest("Product", "Product description", BigDecimal.valueOf(100), 1, 1L);

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.save(request)).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void shouldReturnUpdatedProduct() {
        CreateProductRequest request = new CreateProductRequest("New Product", "Product description", BigDecimal.valueOf(100), 1, 1L);
        Category category = new Category("Category");
        Product product = new Product("Product", "Product description", BigDecimal.valueOf(100), 1, category);

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse returnedProduct = productService.update(request, product.getId());

        assertThat(returnedProduct.name()).isEqualTo(product.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundOnProductPut() {
        CreateProductRequest request = new CreateProductRequest("Product", "Product description", BigDecimal.valueOf(100), 1, 1L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(request, 1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnProductPut() {
        CreateProductRequest request = new CreateProductRequest("Product", "Product description", BigDecimal.valueOf(100), 1, 1L);
        Product product = new Product();

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(request, 1L)).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void shouldDeleteProduct() {
        Category category = new Category("Category");
        Product product = new Product("Product", "Product description", BigDecimal.valueOf(10L), 10, category);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        Mockito.verify(productRepository).delete(product);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundOnProductDelete() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

}