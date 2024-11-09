package fr.univ_rouen.categorymanagement.specification;


import fr.univ_rouen.categorymanagement.model.Category;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CategorySpecifications {

    public static Specification<Category> isRootCategory() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("parent"));
    }

    public static Specification<Category> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Category> createdBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Category> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
    }
    public static Specification<Category> isParentCategory() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotEmpty(root.get("children"));
    }

    public static Specification<Category> isNotParentCategory() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isEmpty(root.get("children"));
    }
}

