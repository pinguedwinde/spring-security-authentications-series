package org.silga.oauth2jwt.security;

import org.jboss.logging.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestOAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger LOGGER = Logger.getLogger(RestOAuth2AuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOGGER.error(authException.getMessage());
        LOGGER.debug("Pre-authenticated : JwtAuthenticationEntryPoint called. Rejecting access");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
