package com.fsoft.ecommerce.service;

import com.fsoft.ecommerce.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Page<CategoryDTO> getAllCategories(Pageable pageable);
    Optional<CategoryDTO> getCategoryById(Long id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    Optional<CategoryDTO> updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    List<String> getAllCategoryNames();
}
