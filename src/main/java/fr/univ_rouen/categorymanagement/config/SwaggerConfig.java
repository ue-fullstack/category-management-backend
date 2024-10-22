package fr.univ_rouen.categorymanagement.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("UE Full Stack API")
                        .version("1.0")
                        .description("Documentation de l'API pour le projet UE Full Stack"));
    }
}
