package fr.univ_rouen.categorymanagement.service;

import fr.univ_rouen.categorymanagement.dto.CategoryDTO;
import fr.univ_rouen.categorymanagement.exceptions.CategoryNotFoundException;
import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.repository.CategoryRepository;
import fr.univ_rouen.categorymanagement.util.CategoryCodeGenerator;
import fr.univ_rouen.categorymanagement.util.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryCodeGenerator codeGenerator;

    @Autowired
    private CategoryRepository categoryRepository;


    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setCode(codeGenerator.generateCode());
        category.setDescription(categoryDTO.getDescription());
        category.setImageUrl(categoryDTO.getImageUrl());
        category.setCreatedAt(LocalDateTime.now());
        category.setRoot(categoryDTO.getParentId() == null);

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setImageUrl(categoryDTO.getImageUrl());

        if (categoryDTO.getParentId() != null && !categoryDTO.getParentId().equals(category.getParent().getId())) {
            Category newParent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            category.setParent(newParent);
            category.setRoot(false);
        } else if (categoryDTO.getParentId() == null && !category.isRoot()) {
            category.setParent(null);
            category.setRoot(true);
        }

        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::convertToDTO);
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return convertToDTO(category);
    }

    public Page<CategoryDTO> searchCategories(Boolean isRoot, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Category> categories;

        if (isRoot != null && startDate != null && endDate != null) {
            categories = categoryRepository.findByRootAndCreatedAtBetween(isRoot, startDate, endDate, pageable);
        } else if (isRoot != null) {
            categories = categoryRepository.findByRoot(isRoot, pageable);
        } else if (startDate != null && endDate != null) {
            categories = categoryRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        } else {
            categories = categoryRepository.findAll(pageable);
        }

        return categories.map(this::convertToDTO);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCode(category.getCode());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setRoot(category.isRoot());

        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }

        List<CategoryDTO> children = category.getChildren().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dto.setChildren(children);

        return dto;
    }
}

