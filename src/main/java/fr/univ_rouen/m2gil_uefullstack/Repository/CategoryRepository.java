package fr.univ_rouen.m2gil_uefullstack.Repository;

import fr.univ_rouen.m2gil_uefullstack.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
}
