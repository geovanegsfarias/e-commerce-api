package com.github.geovanegsfarias.category;

import com.github.geovanegsfarias.commons.CategoryUtils;
import com.github.geovanegsfarias.exception.ResourceAlreadyExistsException;
import com.github.geovanegsfarias.exception.ResourceInUseException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.product.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private CategoryService categoryService;
    private final CategoryUtils utils = new CategoryUtils();

    @Test
    @DisplayName("findAll returns a list with all categories")
    @Order(1)
    void findAll_ReturnsAllCategories_WhenSuccessful() {
        var expectedCategories = utils.newCategoryList();

        BDDMockito.when(categoryRepository.findAll()).thenReturn(expectedCategories);

        var categories = categoryService.findAll();

        Assertions.assertThat(categories).hasSameElementsAs(expectedCategories);
    }

    @Test
    @DisplayName("findById returns a category with given id")
    @Order(2)
    void findById_ReturnsCategory_WhenSuccessful() {
        var expectedCategory = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(expectedCategory.getId())).thenReturn(Optional.of(expectedCategory));

        var category = categoryService.findByIdOrThrowException(expectedCategory.getId());

        Assertions.assertThat(expectedCategory).isEqualTo(category);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when category is not found")
    @Order(3)
    void findById_ThrowsResourceNotFoundException_WhenCategoryNotFound() {
        var savedCategory = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(savedCategory.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> categoryService.findByIdOrThrowException(savedCategory.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Category not found");
    }

    @Test
    @DisplayName("save creates a category")
    @Order(4)
    void save_CreatesCategory_WhenSuccessful() {
        var categoryToSave = utils.newCategoryToSave();
        var expectedSavedCategory = utils.savedCategory();

        BDDMockito.when(categoryRepository.save(categoryToSave)).thenReturn(expectedSavedCategory);

        var savedCategory = categoryService.save(categoryToSave);

        Assertions.assertThat(savedCategory).isEqualTo(expectedSavedCategory);
    }

    @Test
    @DisplayName("save throws ResourceAlreadyExistsException when category name is already in use")
    @Order(5)
    void save_throwsResourceAlreadyExistsException_WhenCategoryNameAlreadyInUse() {
        var categoryToSave = utils.newCategoryToSave();

        BDDMockito.when(categoryRepository.existsByNameIgnoreCase(categoryToSave.getName())).thenReturn(true);

        Assertions.assertThatException()
                .isThrownBy(() -> categoryService.save(categoryToSave))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .withMessage("Category name already in use");
    }

    @Test
    @DisplayName("update updates a category")
    @Order(6)
    void update_UpdatesCategory_WhenSuccessful() {
        var categoryToUpdate = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(categoryToUpdate.getId())).thenReturn(Optional.of(categoryToUpdate));
        BDDMockito.when(categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryToUpdate.getName(), categoryToUpdate.getId())).thenReturn(false);

        Assertions.assertThatNoException().isThrownBy(() -> categoryService.update(categoryToUpdate));
        BDDMockito.verify(categoryRepository).save(categoryToUpdate);
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException when category is not found")
    @Order(7)
    void update_ThrowsResourceNotFoundException_WhenCategoryNotFound() {
        var categoryToUpdate = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(categoryToUpdate.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> categoryService.update(categoryToUpdate))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Category not found");
    }

    @Test
    @DisplayName("update throws ResourceAlreadyExistsException when category name is already in use")
    @Order(8)
    void update_throwsResourceAlreadyExistsException_WhenCategoryNameAlreadyInUse() {
        var categoryToUpdate = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(categoryToUpdate.getId())).thenReturn(Optional.of(categoryToUpdate));
        BDDMockito.when(categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryToUpdate.getName(), categoryToUpdate.getId())).thenReturn(true);

        Assertions.assertThatException()
                .isThrownBy(() -> categoryService.update(categoryToUpdate))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .withMessage("Category name already in use");
    }

    @Test
    @DisplayName("delete removes a category")
    @Order(9)
    void delete_RemovesCategory_WhenSuccessful() {
        var categoryToDelete = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(categoryToDelete.getId())).thenReturn(Optional.of(categoryToDelete));
        BDDMockito.when(productRepository.existsByCategoryId(categoryToDelete.getId())).thenReturn(false);
        BDDMockito.doNothing().when(categoryRepository).delete(categoryToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> categoryService.delete(categoryToDelete.getId()));
        BDDMockito.verify(categoryRepository).delete(categoryToDelete);
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when category is not found")
    @Order(10)
    void delete_ThrowsResourceNotFoundException_WhenCategoryNotFound() {
        var categoryToDelete = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(categoryToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> categoryService.delete(categoryToDelete.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Category not found");
    }

    @Test
    @DisplayName("delete throws ResourceInUseException when category has associated products")
    @Order(11)
    void delete_ThrowsResourceInUseException_WhenCategoryHasAssociatedProducts() {
        var categoryToDelete = utils.savedCategory();

        BDDMockito.when(categoryRepository.findById(categoryToDelete.getId())).thenReturn(Optional.of(categoryToDelete));
        BDDMockito.when(productRepository.existsByCategoryId(categoryToDelete.getId())).thenReturn(true);

        Assertions.assertThatException()
                .isThrownBy(() -> categoryService.delete(categoryToDelete.getId()))
                .isInstanceOf(ResourceInUseException.class)
                .withMessage("Category cannot be deleted because it has associated products");

    }
}