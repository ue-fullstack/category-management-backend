package fr.univ_rouen.categorymanagement.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {
    @GetMapping("/")
    public Map<String, String> index() {
        Map<String, String> response = new HashMap<>();
        response.put("dev1", "DJABIA Venance");
        response.put("dev2", "NASSALANG Michel");
        return response;
    }
}
