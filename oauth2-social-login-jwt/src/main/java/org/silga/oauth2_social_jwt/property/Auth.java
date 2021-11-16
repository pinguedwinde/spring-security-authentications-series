package org.silga.oauth2_social_jwt.property;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class Auth {
    @NotEmpty
    String tokenSecret;
    @NotEmpty
    long tokenExpirationMsec;
}
