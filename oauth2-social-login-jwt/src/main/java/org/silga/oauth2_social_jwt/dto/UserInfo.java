package org.silga.oauth2_social_jwt.dto;

import lombok.Value;

import java.util.List;

@Value
public class UserInfo {
    String id, displayName, email;
    List<String> roles;
}
