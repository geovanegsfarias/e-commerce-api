package com.github.geovanegsfarias.product;

import com.github.geovanegsfarias.category.CategoryService;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product findByIdOrThrowException(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product save(Product productToSave, Long categoryId) {
        var category = categoryService.findByIdOrThrowException(categoryId);
        productToSave.setCategory(category);
        return productRepository.save(productToSave);
    }

    public void update(Product productToUpdate, Long categoryId) {
        var savedProduct = findByIdOrThrowException(productToUpdate.getId());
        var category = categoryService.findByIdOrThrowException(categoryId);
        savedProduct.setName(productToUpdate.getName());
        savedProduct.setDescription(productToUpdate.getDescription());
        savedProduct.setPrice(productToUpdate.getPrice());
        savedProduct.setStock(productToUpdate.getStock());
        savedProduct.setCategory(category);
        productRepository.save(savedProduct);
    }

    public void delete(Long id) {
        var productToDelete = findByIdOrThrowException(id);
        productRepository.delete(productToDelete);
    }

}
