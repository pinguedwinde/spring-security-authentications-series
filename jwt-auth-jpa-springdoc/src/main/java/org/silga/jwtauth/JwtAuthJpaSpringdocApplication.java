package org.silga.jwtauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan("org.silga.jwtauth.config")
public class JwtAuthJpaSpringdocApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthJpaSpringdocApplication.class, args);
    }

}
