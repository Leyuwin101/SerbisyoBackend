package com.example.serbisyofullstack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation for the /api/v1 surface. Documents the Bearer JWT
 * scheme used by all authenticated endpoints.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI serbisyoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Serbisyo API")
                        .description("Trusted local-service marketplace backend")
                        .version("v1"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
