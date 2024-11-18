package fr.univ_rouen.categorymanagement.util;

import fr.univ_rouen.categorymanagement.Controller.CategoryController;
import fr.univ_rouen.categorymanagement.model.Category;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.data.web.PagedResourcesAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<Category, EntityModel<Category>> {
    @Override
    public EntityModel<Category> toModel(Category category) {
        return EntityModel.of(category,
                linkTo(methodOn(CategoryController.class).getCategoryById(category.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories(0, 10, null)).withRel("categories"));
    }
}
