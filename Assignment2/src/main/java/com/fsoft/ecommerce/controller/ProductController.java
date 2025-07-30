package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.dto.ProductDTO;
import com.fsoft.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "Get all products", description = "Get paginated list of all products")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of products returned successfully")
    })
    @GetMapping
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @Operation(summary = "Search products by name", description = "Search products by name with pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    @GetMapping("/search")
    public Page<ProductDTO> searchProducts(
            @Parameter(description = "Product name to search for") @RequestParam String name,
            Pageable pageable) {
        return productService.searchProducts(name, pageable);
    }

    @Operation(summary = "Advanced product search", description = "Search products with multiple criteria")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    @GetMapping("/search/advanced")
    public Page<ProductDTO> advancedSearchProducts(
            @Parameter(description = "Product name to search for") @RequestParam(required = false) String name,
            @Parameter(description = "Product category") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        return productService.searchProducts(name, category, minPrice, maxPrice, pageable);
    }

    @Operation(summary = "Full-text search", description = "Full-text search with relevance scoring")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    @GetMapping("/search/fulltext")
    public List<ProductDTO> fullTextSearch(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        return productService.fullTextSearch(searchTerm);
    }

    @Operation(summary = "Get product by ID", description = "Get a product by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create product", description = "Create a new product")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created successfully")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody ProductDTO dto) {
        return productService.createProduct(dto);
    }

    @Operation(summary = "Update product", description = "Update an existing product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        return productService.updateProduct(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
