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

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(category -> CategoryMapper.toCategoryResponse(category)).toList();
    }

    public CategoryResponse getById(Long id) {
        return CategoryMapper.toCategoryResponse(
                categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."))
        );
    }

    public CategoryResponse save(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResourceAlreadyExistsException("Category name already in use.");
        }

        return CategoryMapper.toCategoryResponse(
                categoryRepository.save(CategoryMapper.toCategory(request))
        );
    }

    public CategoryResponse update(CreateCategoryRequest request, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new ResourceAlreadyExistsException("Category name already in use.");
        }

        category.setName(request.name());
        return CategoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        if (productRepository.existsByCategoryId(id)) {
            throw new ResourceInUseException("Category has products linked to it.");
        }

        categoryRepository.delete(category);
    }

}