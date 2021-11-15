package org.silga.oauth2jwt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringdocConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        val securitySchemeName = "Auth JWT";
        return new OpenAPI()
                .info(new Info().title("Form Login JPA with Springdoc API")
                        .description("Form Login JPA with Springdoc API")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://localhost:8080")))
                .externalDocs(new ExternalDocumentation()
                        .description("Member shipping API")
                        .url("http://localhost:8080"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

}
