package org.silga.oauth2jwt.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@ConfigurationProperties(prefix = "app")
@ConstructorBinding
@Getter
public class AppPropertiesConfig {

    @NotEmpty
    private final String secret;

    public AppPropertiesConfig(String secret) {
        this.secret = secret;
    }
}
