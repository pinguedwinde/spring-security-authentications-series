package org.silga.oauth2_social_jwt.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
    private Long userID;

    private String providerUserId;

    @NotEmpty
    private String displayName;

    @NotEmpty
    private String email;

    private SocialProvider socialProvider;

    @Size(min = 6, message = "{Size.userDto.password}")
    private String password;

    @NotEmpty
    private String matchingPassword;

    public SignUpRequest(String providerUserId, String displayName, String email, String password, SocialProvider socialProvider) {
        this.providerUserId = providerUserId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.socialProvider = socialProvider;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder{
        private String providerUserID;
        private String displayName;
        private String email;
        private String password;
        private SocialProvider socialProvider;

        public Builder addProviderUserID(final String userID) {
            this.providerUserID = userID;
            return this;
        }

        public Builder addDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder addEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder addPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder addSocialProvider(final SocialProvider socialProvider) {
            this.socialProvider = socialProvider;
            return this;
        }

        public SignUpRequest build() {
            return new SignUpRequest(providerUserID, displayName, email, password, socialProvider);
        }
    }
}
