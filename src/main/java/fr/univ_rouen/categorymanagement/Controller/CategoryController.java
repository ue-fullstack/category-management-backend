package fr.univ_rouen.categorymanagement.Controller;

import fr.univ_rouen.categorymanagement.dto.CategoryDTO;
import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CategoryDTO> categories = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/{id}/children")
    public ResponseEntity<CategoryDTO> addChildrenToCategory(@PathVariable Long id, @RequestBody List<CategoryDTO> children) {
        CategoryDTO updatedCategory = categoryService.addChildrenToCategory(id, children);
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping("/roots")
    public ResponseEntity<Page<CategoryDTO>> getRootCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CategoryDTO> rootCategories = categoryService.getRootCategories(page, size);
        return ResponseEntity.ok(rootCategories);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CategoryDTO>> searchCategories(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CategoryDTO> categories = categoryService.searchCategoriesByName(name, page, size);
        return ResponseEntity.ok(categories);
    }

}
