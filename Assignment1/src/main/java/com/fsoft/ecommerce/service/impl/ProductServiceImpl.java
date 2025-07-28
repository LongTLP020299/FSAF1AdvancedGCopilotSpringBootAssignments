package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.ProductDTO;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = toEntity(productDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setAverageRating(0.0);
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    public Optional<ProductDTO> updateProduct(Long id, ProductDTO productDTO) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateEntityFromDTO(existingProduct, productDTO);
                    Product updated = productRepository.save(existingProduct);
                    return toDTO(updated);
                });
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable)
                .map(this::toDTO);
    }

    /**
     * Finds a product by ID and returns the entity directly.
     * Throws ProductNotFoundException if product is not found.
     * 
     * @param id the product ID
     * @return the Product entity
     * @throws ProductNotFoundException if product is not found
     */
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
