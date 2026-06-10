package com.github.geovanegsfarias.product;

import com.github.geovanegsfarias.category.CategoryService;
import com.github.geovanegsfarias.commons.CategoryUtils;
import com.github.geovanegsfarias.commons.ProductUtils;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private ProductService productService;
    private final ProductUtils utils = new ProductUtils(new CategoryUtils());

    @Test
    @DisplayName("findAll returns a page with all products")
    @Order(1)
    void findAll_ReturnsAllProducts_WhenSuccessful() {
        var products = utils.newProductList();
        var pageRequest = PageRequest.of(0, 3);
        var expectedPage = new PageImpl<>(products, pageRequest, products.size());

        BDDMockito.when(productRepository.findAll(BDDMockito.any(Pageable.class))).thenReturn(expectedPage);

        var productsPage = productService.findAll(pageRequest);

        Assertions.assertThat(productsPage).isNotNull().hasSameElementsAs(products);
    }

    @Test
    @DisplayName("findById returns a product with given id")
    @Order(2)
    void findById_ReturnsProduct_WhenSuccessful() {
        var expectedProduct = utils.savedProduct();

        BDDMockito.when(productRepository.findById(expectedProduct.getId())).thenReturn(Optional.of(expectedProduct));

        var product = productService.findByIdOrThrowException(expectedProduct.getId());

        Assertions.assertThat(expectedProduct).isEqualTo(product);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when product is not found")
    @Order(3)
    void findById_ThrowsResourceNotFoundException_WhenProductNotFound() {
        var savedProduct = utils.savedProduct();

        BDDMockito.when(productRepository.findById(savedProduct.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> productService.findByIdOrThrowException(savedProduct.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Product not found");
    }

    @Test
    @DisplayName("save creates a product")
    @Order(4)
    void save_CreatesProduct_WhenSuccessful() {
        var productToSave = utils.newProductToSave();
        var expectedSavedProduct = utils.savedProduct();
        var category = productToSave.getCategory();

        BDDMockito.when(categoryService.findByIdOrThrowException(category.getId())).thenReturn(category);
        BDDMockito.when(productRepository.save(productToSave)).thenReturn(expectedSavedProduct);

        var savedProduct = productService.save(productToSave, category.getId());

        Assertions.assertThat(savedProduct).isEqualTo(expectedSavedProduct);
    }

    @Test
    @DisplayName("save throws ResourceNotFoundException when category is not found")
    @Order(5)
    void save_throwsResourceNotFoundException_WhenCategoryNotFound() {
        var productToSave = utils.newProductToSave();
        var category = productToSave.getCategory();

        BDDMockito.given(categoryService.findByIdOrThrowException(category.getId())).willThrow(new ResourceNotFoundException("Category not found"));

        Assertions.assertThatException()
                .isThrownBy(() -> productService.save(productToSave, category.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Category not found");
    }

    @Test
    @DisplayName("update updates a product")
    @Order(6)
    void update_UpdatesProduct_WhenSuccessful() {
        var productToUpdate = utils.savedProduct();
        var category = productToUpdate.getCategory();

        BDDMockito.when(productRepository.findById(productToUpdate.getId())).thenReturn(Optional.of(productToUpdate));
        BDDMockito.when(categoryService.findByIdOrThrowException(category.getId())).thenReturn(category);

        Assertions.assertThatNoException().isThrownBy(() -> productService.update(productToUpdate, category.getId()));
        BDDMockito.verify(productRepository).save(productToUpdate);
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException when product is not found")
    @Order(7)
    void update_ThrowsResourceNotFoundException_WhenProductNotFound() {
        var productToUpdate = utils.savedProduct();
        var category = productToUpdate.getCategory();

        BDDMockito.when(productRepository.findById(productToUpdate.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> productService.update(productToUpdate, category.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Product not found");
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException when category is not found")
    @Order(8)
    void update_ThrowsResourceNotFoundException_WhenCategoryNotFound() {
        var productToUpdate = utils.savedProduct();
        var category = productToUpdate.getCategory();

        BDDMockito.when(productRepository.findById(productToUpdate.getId())).thenReturn(Optional.of(productToUpdate));
        BDDMockito.given(categoryService.findByIdOrThrowException(category.getId())).willThrow(new ResourceNotFoundException("Category not found"));

        Assertions.assertThatException()
                .isThrownBy(() -> productService.update(productToUpdate, category.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Category not found");
    }

    @Test
    @DisplayName("delete removes a product")
    @Order(9)
    void delete_RemovesProduct_WhenSuccessful() {
        var productToDelete = utils.savedProduct();

        BDDMockito.when(productRepository.findById(productToDelete.getId())).thenReturn(Optional.of(productToDelete));
        BDDMockito.doNothing().when(productRepository).delete(productToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> productService.delete(productToDelete.getId()));
        BDDMockito.verify(productRepository).delete(productToDelete);
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when product is not found")
    @Order(10)
    void delete_ThrowsResourceNotFoundException_WhenProductNotFound() {
        var productToDelete = utils.savedProduct();

        BDDMockito.when(productRepository.findById(productToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> productService.delete(productToDelete.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Product not found");
    }

}