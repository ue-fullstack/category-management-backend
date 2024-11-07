package fr.univ_rouen.categorymanagement.dto;

import fr.univ_rouen.categorymanagement.model.Category;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String imageUrl;
    private Long parentId;
    private List<CategoryDTO> children;
    private LocalDateTime createdAt;
    private boolean selected;
    private boolean root;

    public CategoryDTO() {}
}