package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.CategoryDTO;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        // Mock implementation - In real scenario, you'd have a Category entity
        List<String> categories = productRepository.findAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(name -> new CategoryDTO(null, name))
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), categoryDTOs.size());
        
        if (start > categoryDTOs.size()) {
            return new PageImpl<>(List.of(), pageable, categoryDTOs.size());
        }
        
        return new PageImpl<>(categoryDTOs.subList(start, end), pageable, categoryDTOs.size());
    }

    @Override
    public Optional<CategoryDTO> getCategoryById(Long id) {
        // Mock implementation
        return Optional.empty();
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Mock implementation
        return categoryDTO;
    }

    @Override
    public Optional<CategoryDTO> updateCategory(Long id, CategoryDTO categoryDTO) {
        // Mock implementation
        return Optional.of(categoryDTO);
    }

    @Override
    public void deleteCategory(Long id) {
        // Mock implementation
    }

    @Override
    public List<String> getAllCategoryNames() {
        return productRepository.findAllCategories();
    }
}
