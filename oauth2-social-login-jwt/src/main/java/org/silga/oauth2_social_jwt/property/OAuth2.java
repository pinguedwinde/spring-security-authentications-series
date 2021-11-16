package org.silga.oauth2_social_jwt.property;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class OAuth2 {
    @NotEmpty
    List<String> authorizedRedirectUris;
}
