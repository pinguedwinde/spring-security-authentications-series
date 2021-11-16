package org.silga.oauth2_social_jwt.config;

import lombok.Value;
import org.silga.oauth2_social_jwt.property.Auth;
import org.silga.oauth2_social_jwt.property.OAuth2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
@Value
public class AppProperties {
    Auth auth;
    OAuth2 oauth2;
}
