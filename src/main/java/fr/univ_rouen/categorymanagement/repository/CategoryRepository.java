package fr.univ_rouen.categorymanagement.repository;

import fr.univ_rouen.categorymanagement.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findByParentIsNull(Pageable pageable);
}