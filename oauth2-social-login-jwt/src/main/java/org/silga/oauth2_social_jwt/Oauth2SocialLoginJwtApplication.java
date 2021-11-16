package org.silga.oauth2_social_jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.silga.oauth2_social_jwt.config")
public class Oauth2SocialLoginJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2SocialLoginJwtApplication.class, args);
    }

}
