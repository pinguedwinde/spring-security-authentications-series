package org.silga.oauth2_social_jwt.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.silga.oauth2_social_jwt.dto.SignUpRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignUpRequest> {

    @Override
    public boolean isValid(final SignUpRequest user, final ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
