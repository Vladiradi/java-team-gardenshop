package telran.project.gardenshop.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                final String schemeName = "bearerAuth";

                return new OpenAPI()
                                .info(new Info()
                                                .title("Garden Shop API")
                                                .version("1.0")
                                                .description("Documentation REST API for GardenShop project"))
                                .components(new Components()
                                                .addSecuritySchemes(schemeName, new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")))
                                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                                .tags(Arrays.asList(
                                                new Tag().name("Users").description("User management operations"),
                                                new Tag().name("Auth").description("Authentication operations"),
                                                new Tag().name("Categories")
                                                                .description("Category management operations"),
                                                new Tag().name("Products").description("Product management operations"),
                                                new Tag().name("Favorites")
                                                                .description("User favorites management operations"),
                                                new Tag().name("Cart")
                                                                .description("Shopping cart management operations"),
                                                new Tag().name("Orders").description("Order management operations"),
                                                new Tag().name("Payment").description("Payment management operations"),
                                                new Tag().name("Reports").description(
                                                                "Business analytics and reporting operations")));
        }
}
