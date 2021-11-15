package org.silga.oauth2jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.silga.oauth2jwt.config")
public class Oauth2LoginJwtGoogleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2LoginJwtGoogleApplication.class, args);
    }

}
