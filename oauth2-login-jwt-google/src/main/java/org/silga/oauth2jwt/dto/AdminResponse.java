package org.silga.oauth2jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminResponse {
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;
}
