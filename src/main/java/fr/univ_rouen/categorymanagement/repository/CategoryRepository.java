package fr.univ_rouen.categorymanagement.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findByParentIsNull(Pageable pageable);
}