package com.github.geovanegsfarias.category;

import com.github.geovanegsfarias.exception.ResourceAlreadyExistsException;
import com.github.geovanegsfarias.exception.ResourceInUseException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findByIdOrThrowException(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Category save(Category categoryToSave) {
        assertCategoryNameIsAvailable(categoryToSave.getName());
        return categoryRepository.save(categoryToSave);
    }

    public void update(Category categoryToUpdate) {
        var savedCategory = findByIdOrThrowException(categoryToUpdate.getId());
        assertCategoryNameIsAvailable(categoryToUpdate.getName(), categoryToUpdate.getId());
        savedCategory.setName(categoryToUpdate.getName());
        categoryRepository.save(savedCategory);
    }

    public void delete(Long id) {
        var categoryToDelete = findByIdOrThrowException(id);
        assertCategoryHasNoProducts(id);
        categoryRepository.delete(categoryToDelete);
    }

    private void assertCategoryNameIsAvailable(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) throw new ResourceAlreadyExistsException("Category name already in use");
    }

    private void assertCategoryNameIsAvailable(String name, Long id) {
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) throw new ResourceAlreadyExistsException("Category name already in use");
    }

    private void assertCategoryHasNoProducts(Long id) {
        if (productRepository.existsByCategoryId(id)) throw new ResourceInUseException("Category has associated products.");
    }

}