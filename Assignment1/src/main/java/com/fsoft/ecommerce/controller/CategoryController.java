package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.dto.CategoryDTO;
import com.fsoft.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Get paginated list of all categories")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of categories returned successfully")
    })
    @GetMapping
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return categoryService.getAllCategories(pageable);
    }

    @Operation(summary = "Get category by ID", description = "Get a category by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create category", description = "Create a new category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category created successfully")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDTO createCategory(@RequestBody CategoryDTO dto) {
        return categoryService.createCategory(dto);
    }

    @Operation(summary = "Update category", description = "Update an existing category by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return categoryService.updateCategory(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete category", description = "Delete a category by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
