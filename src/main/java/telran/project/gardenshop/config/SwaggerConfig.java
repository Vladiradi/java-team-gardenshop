package telran.project.gardenshop.config;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Garden Shop API")
                        .version("1.0")
                        .description("Документация REST API для Garden Shop проекта"));
    }
}