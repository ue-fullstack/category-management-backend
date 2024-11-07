package fr.univ_rouen.categorymanagement.repository;

import fr.univ_rouen.categorymanagement.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByRootAndCreatedAtBetween(Boolean isRoot, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Category> findByRoot(boolean isRoot, Pageable pageable);
    Page<Category> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Trouver les catégories racines (sans parent)
    Page<Category> findByParentIsNull(Pageable pageable);

    // Recherche de catégories par nom (insensible à la casse)
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Vérifier si une catégorie est enfant d'elle-même
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.id = :id AND c.parent.id = :id")
    boolean isCategoryChildOfItself(Long id);

    // Trouver les enfants directs d'une catégorie
    List<Category> findByParentId(Long parentId);

    // Vérifier si une catégorie a des enfants
    boolean existsByParentId(Long parentId);

    // recuperer touts les enfants qui n'ont pas de parent
    Page<Category> findBySelectedFalse(Pageable pageable);


}
