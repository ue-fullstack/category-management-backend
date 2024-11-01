package fr.univ_rouen.categorymanagement.Controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
public class IndexController {

    /**
     * Retourne une liste de développeurs avec des attributs `id` et `name`.
     *
     * @return Une liste de développeurs
     */
    @GetMapping("/")
    public List<Map<String, String>> index() {
        List<Map<String, String>> developers = new ArrayList<>();

        Map<String, String> dev1 = new HashMap<>();
        dev1.put("id", "1");
        dev1.put("name", "Venance DJABIA");

        Map<String, String> dev2 = new HashMap<>();
        dev2.put("id", "2");
        dev2.put("name", "NASSALANG Michel");

        developers.add(dev1);
        developers.add(dev2);

        return developers;
    }
}