package fr.univ_rouen.categorymanagement.service;

import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.repository.CategoryRepository;
import fr.univ_rouen.categorymanagement.specification.CategorySpecifications;
import fr.univ_rouen.categorymanagement.util.CategoryCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


@Service
public class CategoryService {

    @Autowired
    private CategoryCodeGenerator categoryCodeGenerator;

    @Autowired
    private CategoryRepository categoryRepository;


    // Créer une nouvelle catégorie
    public Category createCategory(Category category) {
        // Vérifications existantes
        if (category.getParent() != null && category.getParent().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent");
        }
        if(category.getName() == null || category.getName().isEmpty()) {
            throw new IllegalArgumentException("Une catégorie doit obligatoirement avoir un nom");
        }
    
        // Générer un code unique
        String code = categoryCodeGenerator.generateCode(category.getName());
        category.setCode(code);
        // Vérifier si une catégorie avec le même nom existe déjà
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà");
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

    public List<Category> getCategoriesByIds(List<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    // Modifier une catégorie existante
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        if (categoryDetails.getParent() != null && categoryDetails.getParent().getId().equals(id)) {
            throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent.");
        }
        // Mise à jour des champs non-null
        if (categoryDetails.getName() != null) {
            category.setName(categoryDetails.getName());
        }
        if (categoryDetails.getCode() != null) {
            category.setCode(categoryDetails.getCode());
        }
        if (categoryDetails.getDescription() != null) {
            category.setDescription(categoryDetails.getDescription());
        }
        if (categoryDetails.getImageUrl() != null) {
            category.setImageUrl(categoryDetails.getImageUrl());
        }

        // Gestion du parent
        if (categoryDetails.getParent() != null) {
            Category newParent = getCategoryById(categoryDetails.getParent().getId());
            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);

        // Récupérer la catégorie mise à jour depuis la base de données
        return getCategoryById(category.getId());
    }

    // Supprimer une catégorie
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    // Recherche des catégories par nom avec pagination
    public Page<Category> searchCategoriesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    }


    public Page<Category> searchCategories(Boolean isRoot,
                                           LocalDateTime afterDate,
                                           LocalDateTime beforeDate,
                                           Boolean isParent,
                                           String sortBy,
                                           boolean ascending,
                                           int page, int size) {
        Specification<Category> spec = Specification.where(null);
        // Pour les catégories root
        if (isRoot != null && isRoot) {
            spec = spec.and(CategorySpecifications.isRootCategory());
        }
        // Pour les catégories créées avant la date
        if (afterDate != null) {
            spec = spec.and(CategorySpecifications.createdAfter(afterDate));
        }
        // Pour les catégories créées après la date
        if (beforeDate != null) {
            spec = spec.and(CategorySpecifications.createdBefore(beforeDate));
        }
        // Pour les catégories parent ou non parent
        if (isParent != null) {
            spec = isParent ? spec.and(CategorySpecifications.isParentCategory()) : spec.and(CategorySpecifications.isNotParentCategory());
        }
        Sort sort;
        switch (sortBy) {
            case "name":
                sort = ascending ? Sort.by("name").ascending() : Sort.by("name").descending();
                break;
            case "createdAt":
                sort = ascending ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
                break;
            default:
                sort = Sort.unsorted();
        }
        Pageable pageable = PageRequest.of(page, size).withSort(sort);
        // Effectuer la recherche avec la spécification et la pagination
        if ("childrenCount".equals(sortBy)) {
            // Si on trie par childrenCount, utiliser la méthode avec tri par nombre d'enfants
            if (ascending) {
                return categoryRepository.findAllCategoriesSortedByChildrenCount(pageable);
            } else {
                return categoryRepository.findAllCategoriesSortedByChildrenCountDesc(pageable);
            }
        }
        return categoryRepository.findAll(spec, pageable);
    }
}

