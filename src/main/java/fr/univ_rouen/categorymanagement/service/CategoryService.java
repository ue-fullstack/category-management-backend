package fr.univ_rouen.categorymanagement.service;

import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Créer une nouvelle catégorie
    public Category createCategory(Category category) {
        if (category.getParent() != null && category.getParent().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent");
        }
        return categoryRepository.save(category);
    }

    // Lister toutes les catégories avec pagination
    public Page<Category> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable);
    }

    // Récupérer les catégories racines avec pagination
    public Page<Category> getRootCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findByParentIsNull(pageable);
    }

    // Récupérer une catégorie par ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
    }

    // Modifier une catégorie existante
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);

        if (categoryDetails.getParent() != null && categoryDetails.getParent().getId().equals(id)) {
            throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent.");
        }

        category.setName(categoryDetails.getName());
        category.setParent(categoryDetails.getParent());

        return categoryRepository.save(category);
    }

    // Supprimer une catégorie
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}