package fr.univ_rouen.categorymanagement.util;

import fr.univ_rouen.categorymanagement.dto.CategoryDTO;
import fr.univ_rouen.categorymanagement.model.Category;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setCreatedAt(category.getCreatedAt());
        dto.setSelected(category.isSelected());
        dto.setRoot(category.isRoot());

        if (category.getChildren() != null) {
            dto.setChildren(category.getChildren().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setCreatedAt(dto.getCreatedAt());
        category.setSelected(dto.isSelected());

        // Ne pas définir le parent ou les enfants ici pour éviter les boucles infinies
        // Gérez ces relations séparément dans le service

        return category;
    }
}
