package fr.univ_rouen.categorymanagement.util;

import org.springframework.stereotype.Component;
import fr.univ_rouen.categorymanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

@Component
public class CategoryCodeGenerator {
    private static final String PREFIX = "CAT";
    private static final int CODE_LENGTH = 6;
    private final Random random = new Random();

    @Autowired
    private CategoryRepository categoryRepository;

    public String generateCode(String name) {
        String baseCode = generateBaseCode(name);
        String uniqueCode = baseCode;
        int suffix = 1;

        while (categoryRepository.existsByCode(uniqueCode)) {
            uniqueCode = baseCode + String.format("%02d", suffix);
            suffix++;
        }

        return uniqueCode;
    }

    private String generateBaseCode(String name) {
        StringBuilder code = new StringBuilder(PREFIX);

        // Utiliser les trois premières lettres du nom (ou moins si le nom est plus court)
        String namePrefix = name.replaceAll("[^A-Za-z]", "").toUpperCase();
        code.append(namePrefix.substring(0, Math.min(namePrefix.length(), 3)));

        // Ajouter des caractères aléatoires si nécessaire
        while (code.length() < CODE_LENGTH) {
            code.append((char) (random.nextInt(26) + 'A'));
        }

        return code.toString();
    }
}
