package org.silga.oauth2_social_jwt.dto;

import lombok.Value;

@Value
public class JwtAuthenticationResponse {
    String accessToken;
    UserInfo user;
}
