package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.request.CreateCategoryRequest;
import com.geovane.e_commerce_api.dto.response.CategoryResponse;
import com.geovane.e_commerce_api.exception.ResourceAlreadyExistsException;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.mapper.CategoryMapper;
import com.geovane.e_commerce_api.model.Category;
import com.geovane.e_commerce_api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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

        categoryRepository.delete(category);
    }

}