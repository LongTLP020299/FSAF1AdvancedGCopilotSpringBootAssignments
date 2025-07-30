package com.fsoft.ecommerce.service;

import com.fsoft.ecommerce.dto.ProductDTO;
import com.fsoft.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Page<ProductDTO> getAllProducts(Pageable pageable);
    Optional<ProductDTO> getProductById(Long id);
    ProductDTO createProduct(ProductDTO productDTO);
    Optional<ProductDTO> updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    Page<ProductDTO> getProductsByCategory(String category, Pageable pageable);
    Product findProductById(Long id); // Returns Product entity directly, throws ProductNotFoundException if not found
    
    // Optimized search methods
    Page<ProductDTO> searchProducts(String name, Pageable pageable);
    Page<ProductDTO> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    List<ProductDTO> fullTextSearch(String searchTerm);
}
