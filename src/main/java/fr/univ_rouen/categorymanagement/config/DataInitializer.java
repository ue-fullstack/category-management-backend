package fr.univ_rouen.categorymanagement.config;

import fr.univ_rouen.categorymanagement.model.Category;
import fr.univ_rouen.categorymanagement.repository.CategoryRepository;
import fr.univ_rouen.categorymanagement.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(CategoryService categoryService, CategoryRepository categoryRepository) {
        return args -> {
            // Vider la base de données
            categoryRepository.deleteAll();
            System.out.println("Toutes les catégories existantes ont été supprimées.");

            // Catégories racines
            Category electronics = createCategory(categoryService, "Électronique",
                    "Tous les produits électroniques", "https://example.com/electronics.jpg", null);

            Category fashion = createCategory(categoryService, "Mode",
                    "Vêtements et accessoires de mode", "https://example.com/fashion.jpg", null);

            Category homeAndGarden = createCategory(categoryService, "Maison et Jardin",
                    "Produits pour la maison et le jardin", "https://example.com/home_garden.jpg", null);

            // Sous-catégories pour Électronique
            Category phones = createCategory(categoryService, "Téléphones",
                    "Smartphones et accessoires", "https://example.com/phones.jpg", electronics);

            Category laptops = createCategory(categoryService, "Ordinateurs",
                    "Ordinateurs portables et de bureau", "https://example.com/laptops.jpg", electronics);

            Category cameras = createCategory(categoryService, "Caméras",
                    "Appareils photo et caméras vidéo", "https://example.com/cameras.jpg", electronics);

            // Sous-catégories pour Mode
            Category mensWear = createCategory(categoryService, "Vêtements Homme",
                    "Mode masculine", "https://example.com/mens_wear.jpg", fashion);

            Category womensWear = createCategory(categoryService, "Vêtements Femme",
                    "Mode féminine", "https://example.com/womens_wear.jpg", fashion);

            Category shoes = createCategory(categoryService, "Chaussures",
                    "Chaussures pour tous", "https://example.com/shoes.jpg", fashion);

            // Sous-catégories pour Maison et Jardin
            Category furniture = createCategory(categoryService, "Mobilier",
                    "Mobilier d'intérieur et d'extérieur", "https://example.com/furniture.jpg", homeAndGarden);

            Category gardening = createCategory(categoryService, "Jardinage",
                    "Outils et plantes pour le jardin", "https://example.com/gardening.jpg", homeAndGarden);

            Category kitchen = createCategory(categoryService, "Cuisine",
                    "Équipements et ustensiles de cuisine", "https://example.com/kitchen.jpg", homeAndGarden);

            Category phoneAccessories = createCategory(categoryService, "Accessoires de téléphones",
                    "Coques, chargeurs et autres accessoires pour téléphones", "https://example.com/phone_accessories.jpg", phones);

            Category laptopAccessories = createCategory(categoryService, "Accessoires de ordinateurs",
                    "Souris, claviers et autres accessoires pour ordinateurs", "https://example.com/laptop_accessories.jpg", laptops);

            // Ajouter des sous-catégories pour les accessoires de téléphones
            createCategory(categoryService, "Coques", "Coques pour téléphones", "https://example.com/cases.jpg", phoneAccessories);
            createCategory(categoryService, "Chargeurs", "Chargeurs pour téléphones", "https://example.com/chargers.jpg", phoneAccessories);
            createCategory(categoryService, "Écouteurs", "Écouteurs et casques", "https://example.com/earphones.jpg", phoneAccessories);

            // Ajouter des sous-catégories pour les accessoires de ordinateurs
            createCategory(categoryService, "Souris", "Souris pour ordinateurs", "https://example.com/mice.jpg", laptopAccessories);
            createCategory(categoryService, "Claviers", "Claviers pour ordinateurs", "https://example.com/keyboards.jpg", laptopAccessories);
            createCategory(categoryService, "Hubs USB", "Hubs et adaptateurs USB", "https://example.com/usb_hubs.jpg", laptopAccessories);


            System.out.println("Les données de test ont été initialisées avec succès !");
        };
    }

    private Category createCategory(CategoryService categoryService, String name, String description, String imageUrl, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setImageUrl(imageUrl);
        category.setParent(parent);
        return categoryService.createCategory(category);
    }
}
