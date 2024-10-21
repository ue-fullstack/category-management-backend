package fr.univ_rouen.m2gil_uefullstack.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private LocalDateTime creationDate;
    private List<Long> childrenIds;
    private boolean isRoot;
}
