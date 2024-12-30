package fr.univ_rouen.categorymanagement.repository;

import fr.univ_rouen.categorymanagement.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Page<Category> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Trouver les catégories racines (sans parent)
    Page<Category> findByParentIsNull(Pageable pageable);

    // Recherche de catégories par nom (insensible à la casse)
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);
    boolean existsByCode(String code);

    @Modifying
    @Query("UPDATE Category c SET c.parent = null WHERE c.parent.id = :parentId")
    void detachChildrenFromParent(@Param("parentId") Long parentId);



    // Méthode de recherche avec tri par le nombre d'enfants
    @Query("SELECT c FROM Category c LEFT JOIN c.children children " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(children) " +
            "ASC")
    Page<Category> findAllCategoriesSortedByChildrenCount(Pageable pageable);

    @Query("SELECT c FROM Category c LEFT JOIN c.children children " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(children) DESC")
    Page<Category> findAllCategoriesSortedByChildrenCountDesc(Pageable pageable);





}
