package fr.univ_rouen.categorymanagement.service;

import fr.univ_rouen.categorymanagement.dto.CategoryDTO;
import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.repository.CategoryRepository;
import fr.univ_rouen.categorymanagement.util.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(categoryDTO.getName());
        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + id));
        return categoryMapper.toDTO(category);
    }

    public Page<CategoryDTO> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(categoryMapper::toDTO);
    }

    @Transactional
    public CategoryDTO addChildrenToCategory(Long parentId, List<CategoryDTO> childrenDTOs) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Catégorie parent non trouvée avec l'ID : " + parentId));

        for (CategoryDTO childDTO : childrenDTOs) {
            Category child = categoryMapper.toEntity(childDTO);
            parent.addChild(child);
        }

        Category savedParent = categoryRepository.save(parent);
        return categoryMapper.toDTO(savedParent);
    }

    public Page<CategoryDTO> getRootCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findByParentIsNull(pageable);
        return categoryPage.map(categoryMapper::toDTO);
    }

    public Page<CategoryDTO> searchCategoriesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        return categoryPage.map(categoryMapper::toDTO);
    }
}

