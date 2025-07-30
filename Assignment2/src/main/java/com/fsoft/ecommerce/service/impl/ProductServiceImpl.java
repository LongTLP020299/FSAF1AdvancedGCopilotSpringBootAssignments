package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.ProductDTO;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    @CacheEvict(value = {"products", "productsByCategory", "searchProducts", "advancedSearch"}, 
                allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = toEntity(productDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setAverageRating(0.0);
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    @CacheEvict(value = {"product", "product-details", "products", "productsByCategory", "searchProducts", "advancedSearch"}, 
                key = "#id", 
                beforeInvocation = false)
    public Optional<ProductDTO> updateProduct(Long id, ProductDTO productDTO) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateEntityFromDTO(existingProduct, productDTO);
                    Product updated = productRepository.save(existingProduct);
                    return toDTO(updated);
                });
    }

    @Override
    @CacheEvict(value = {"product", "product-details", "products", "productsByCategory", "searchProducts", "advancedSearch"}, 
                key = "#id",
                beforeInvocation = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "productsByCategory", key = "#category + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryOptimized(category, pageable)
                .map(this::toDTO);
    }

    /**
     * Optimized search by name with pagination and caching
     */
    @Override
    @Cacheable(value = "searchProducts", key = "#name + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toDTO);
    }

    /**
     * Advanced search with multiple criteria
     */
    @Override
    @Cacheable(value = "advancedSearch", key = "#name + '-' + #category + '-' + #minPrice + '-' + #maxPrice + '-' + #pageable.pageNumber")
    public Page<ProductDTO> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(name, category, minPrice, maxPrice, pageable)
                .map(this::toDTO);
    }

    /**
     * Full-text search with relevance scoring
     */
    @Override
    @Cacheable(value = "fullTextSearch", key = "#searchTerm")
    public List<ProductDTO> fullTextSearch(String searchTerm) {
        return productRepository.fullTextSearch(searchTerm)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds a product by ID and returns the entity directly.
     * Throws ProductNotFoundException if product is not found.
     * 
     * @param id the product ID
     * @return the Product entity
     * @throws ProductNotFoundException if product is not found
     */
    @Cacheable(value = "product-details", key = "#id")
    public Product findProductById(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.orElseThrow(() -> 
            new ProductNotFoundException("Product not found with id: " + id));
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        dto.setAverageRating(product.getAverageRating());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }

    private Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());
        return product;
    }

    private void updateEntityFromDTO(Product product, ProductDTO dto) {
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getStockQuantity() != null) product.setStockQuantity(dto.getStockQuantity());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
    }
}
