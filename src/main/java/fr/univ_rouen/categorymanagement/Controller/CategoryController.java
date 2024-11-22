package fr.univ_rouen.categorymanagement.Controller;

import fr.univ_rouen.categorymanagement.exceptions.ErrorResponse;
import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.service.CategoryService;
import fr.univ_rouen.categorymanagement.service.ImageService;
import fr.univ_rouen.categorymanagement.util.CategoryModelAssembler;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryModelAssembler categoryAssembler;

    @Autowired
    private ImageService imageService;

    // Créer une nouvelle catégorie
    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "root", defaultValue = "false") boolean root,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "childrenIds", required = false) List<Long> childrenIds) {
        try {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);

            if (parentId != null) {
                Category parentCategory = categoryService.getCategoryById(parentId);
                category.setParent(parentCategory);
            }

            if (image != null && !image.isEmpty()) {
                String imageUrl = imageService.saveImage(image);
                category.setImageUrl(imageUrl);
            }

            if (childrenIds != null && !childrenIds.isEmpty()) {
                List<Category> childrenCategories = categoryService.getCategoriesByIds(childrenIds);
                childrenCategories.forEach(category::addChild);
            }

            Category createdCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);

        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la création de la catégorie: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lister toutes les catégories avec pagination

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Category>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Category> assembler) {
        Page<Category> categories = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(assembler.toModel(categories, categoryAssembler));
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
                    "Catégorie avec l'ID " + id + " non trouvée.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Modifier une catégorie par ID
    @PutMapping("/{id}")
public ResponseEntity<?> updateCategory(
        @PathVariable Long id,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "parentId", required = false) Long parentId,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "childrenIds", required = false) List<Long> childrenIds) {
    try {
        // Récupérer la catégorie existante
        Category existingCategory = categoryService.getCategoryById(id);
        if (existingCategory == null) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND,
                    "Catégorie avec l'ID " + id + " non trouvée.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Mettre à jour les champs si fournis
        if (name != null)
            existingCategory.setName(name);
        if (description != null)
            existingCategory.setDescription(description);

        // Gérer le parent
        if (parentId != null) {
            Category parentCategory = categoryService.getCategoryById(parentId);
            if (parentCategory == null) {
                throw new IllegalArgumentException("Catégorie parent avec l'ID " + parentId + " non trouvée.");
            }
            existingCategory.setParent(parentCategory);
        } else {
            existingCategory.setParent(null); // Explicitement définir le parent à null si parentId n'est pas fourni
        }

        // Gérer l'image
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.saveImage(image);
            existingCategory.setImageUrl(imageUrl);
        }

        // Mettre à jour la catégorie
        Category updatedCategory = categoryService.updateCategory(id, existingCategory);

        // Gérer les enfants si une liste d'IDs est fournie
        if (childrenIds != null) {
            List<Category> childrenCategories = categoryService.getCategoriesByIds(childrenIds);
            updatedCategory.getChildren().clear();
            childrenCategories.forEach(updatedCategory::addChild);
            updatedCategory = categoryService.updateCategory(id, updatedCategory);
        }

        return ResponseEntity.ok(updatedCategory);

    } catch (IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Erreur : " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erreur lors de la mise à jour de la catégorie: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
                    "Catégorie avec l'ID " + id + " non trouvée.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
    public ResponseEntity<PagedModel<EntityModel<Category>>> searchCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isRoot,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime afterDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate,
            @RequestParam(required = false) Boolean isParent,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean ascending,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Category> assembler) {
        Page<Category> categories = categoryService.searchCategories(
                name, isRoot, afterDate, beforeDate, isParent, sortBy, ascending, page, size);
        return ResponseEntity.ok(assembler.toModel(categories, categoryAssembler));
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = imageService.loadImageAsResource(fileName);

        // Déterminer le type de contenu
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Par défaut, utilisez application/octet-stream
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
