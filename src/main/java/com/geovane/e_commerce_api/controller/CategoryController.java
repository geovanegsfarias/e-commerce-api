package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.request.CreateCategoryRequest;
import com.geovane.e_commerce_api.dto.response.CategoryResponse;
import com.geovane.e_commerce_api.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of categories.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single category", description = "Retrieve a specific category by ID.")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Category not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
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
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Update an existing category by ID.")
    @ApiResponse(responseCode = "200", description = "Category successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid request data.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "403", description = "Access Denied.")
    @ApiResponse(responseCode = "404", description = "Category not found.")
    @ApiResponse(responseCode = "409", description = "Category name already in use.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody @Valid CreateCategoryRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.update(request, id));
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
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
