package com.github.geovanegsfarias.service;

import com.github.geovanegsfarias.dto.request.CreateProductRequest;
import com.github.geovanegsfarias.dto.response.ProductResponse;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.mapper.ProductMapper;
import com.github.geovanegsfarias.model.Category;
import com.github.geovanegsfarias.model.Product;
import com.github.geovanegsfarias.repository.CategoryRepository;
import com.github.geovanegsfarias.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> ProductMapper.toProductResponse(product));
    }

    public ProductResponse getById(Long id) {
        return ProductMapper.toProductResponse(
                productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."))
        );
    }

    public ProductResponse save(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        return ProductMapper.toProductResponse(
                productRepository.save(ProductMapper.toProduct(request, category))
        );
    }

    public ProductResponse update(CreateProductRequest request, Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found."));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);
        return ProductMapper.toProductResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        productRepository.delete(product);
    }
}
