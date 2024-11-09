package fr.univ_rouen.categorymanagement.Controller;

import fr.univ_rouen.categorymanagement.exceptions.ErrorResponse;
import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Créer une nouvelle catégorie
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Lister toutes les catégories avec pagination
    @GetMapping
    public ResponseEntity<Page<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categories = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(categories);
    }

    // Récupérer une catégorie par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
             ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND,
                    "Catégorie avec l'ID " + id + " non trouvée."
            );
            return new  ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Modifier une catégorie par ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND,
                    "Erreur lors de la mise à jour: Contraintes de réalisation / ID " + id + " non trouvée."
            );
            return new  ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer une catégorie par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND,
                    "Catégorie avec l'ID " + id + " non trouvée."
            );
            return new  ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

    }

    // Récupérer les catégories racines avec pagination
    @GetMapping("/roots")
    public ResponseEntity<Page<Category>> getRootCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categories = categoryService.getRootCategories(page, size);
        return ResponseEntity.ok(categories);
    }

    // Recherche de catégories par nom avec pagination
    @GetMapping("/searchbyname")
    public ResponseEntity<Page<Category>> searchCategories(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categories = categoryService.searchCategoriesByName(name, page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/search")
    public Page<Category> searchCategories(
            @RequestParam(required = false) Boolean isRoot,
            @RequestParam(required = false) Boolean isParent,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime afterDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean ascending
    ) {
        boolean asc = ascending != null && ascending;
        return categoryService.searchCategories(isRoot, afterDate, beforeDate, isParent, sortBy, asc, page, size);
    }

}
