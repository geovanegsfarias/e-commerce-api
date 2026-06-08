package com.github.geovanegsfarias.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Category")
@Slf4j
public class CategoryController {
    private final CategoryMapper mapper;
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryMapper mapper) {
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of categories.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.debug("Request received to list all categories");

        var categories = categoryService.findAll();

        var categoryResponseList = mapper.toCategoryResponseList(categories);

        return ResponseEntity.ok(categoryResponseList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single category", description = "Retrieve a specific category by ID.")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Category not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.debug("Request received to find category by id {}", id);

        var category = categoryService.findByIdOrThrowException(id);

        var categoryResponse = mapper.toCategoryResponse(category);

        return ResponseEntity.ok(categoryResponse);

    }

    @PostMapping
    @Operation(summary = "Add a new category", description = "Create a new category.")
    @ApiResponse(responseCode = "201", description = "Category successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid request data.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "403", description = "Access Denied.")
    @ApiResponse(responseCode = "409", description = "Category name already in use.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<CategoryResponse> saveCategory(@RequestBody @Valid CreateCategoryRequest request) {
        log.debug("Request received to save category {}", request);

        var categoryToSave = mapper.toCategory(request);

        var savedCategory = categoryService.save(categoryToSave);

        var categoryResponse = mapper.toCategoryResponse(savedCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Update an existing category by ID.")
    @ApiResponse(responseCode = "204", description = "Category successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid request data.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "403", description = "Access Denied.")
    @ApiResponse(responseCode = "404", description = "Category not found.")
    @ApiResponse(responseCode = "409", description = "Category name already in use.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody @Valid CreateCategoryRequest request) {
        log.debug("Request received to update category {}", request);

        var categoryToUpdate = mapper.toCategory(request);

        categoryToUpdate.setId(id);

        categoryService.update(categoryToUpdate);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Delete a specific category by ID.")
    @ApiResponse(responseCode = "204", description = "Category successfully deleted.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "403", description = "Access Denied.")
    @ApiResponse(responseCode = "404", description = "Category not found.")
    @ApiResponse(responseCode = "409", description = "Category has products linked to it.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("Request received to delete category by id {}", id);

        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
