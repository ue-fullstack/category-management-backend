package fr.univ_rouen.m2gil_uefullstack.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
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

    // Lister toutes les catégories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Récupérer une catégorie par ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
    }

    // Supprimer une catégorie
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    // Récupérer les catégories racines (sans parent)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }
}
