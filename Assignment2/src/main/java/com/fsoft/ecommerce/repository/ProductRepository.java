package com.fsoft.ecommerce.repository;

import com.fsoft.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    Page<Product> findByCategory(String category, Pageable pageable);
    
    // Optimized search methods with indexes
    List<Product> findByNameContainingIgnoreCase(String name);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Advanced search with multiple criteria
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "p.stockQuantity > 0 " +
           "ORDER BY p.name ASC")
    Page<Product> searchProducts(@Param("name") String name,
                                @Param("category") String category,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                Pageable pageable);
    
    // Full-text search với relevance scoring
    @Query(value = "SELECT p.*, " +
           "CASE " +
           "WHEN LOWER(p.name) = LOWER(?1) THEN 100 " +
           "WHEN LOWER(p.name) LIKE LOWER(CONCAT(?1, '%')) THEN 90 " +
           "WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) THEN 80 " +
           "WHEN LOWER(p.description) LIKE LOWER(CONCAT('%', ?1, '%')) THEN 70 " +
           "ELSE 60 " +
           "END as relevance_score " +
           "FROM products p " +
           "WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
           "AND p.stock_quantity > 0 " +
           "ORDER BY relevance_score DESC, p.name ASC",
           nativeQuery = true)
    List<Product> fullTextSearch(String searchTerm);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findAvailableProducts();
    
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();
    
    // Optimized category-based search
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.stockQuantity > 0 ORDER BY p.averageRating DESC, p.name ASC")
    Page<Product> findByCategoryOptimized(@Param("category") String category, Pageable pageable);
}
