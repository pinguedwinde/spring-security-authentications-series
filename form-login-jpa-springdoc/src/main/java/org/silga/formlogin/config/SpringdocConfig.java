package org.silga.formlogin.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringdocConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Form Login JPA with Springdoc API")
                        .description("Form Login JPA with Springdoc API")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://localhost:8080")))
                .externalDocs(new ExternalDocumentation()
                        .description("Member shipping API")
                        .url("http://localhost:8080"));
    }

}
