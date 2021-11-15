package org.silga.oauth2jwt.security;

import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static org.silga.oauth2jwt.security.SecurityConstants.AUTHENTICATION_TYPE;

@Component
public class RestOAuth2AuthorizationFilter extends GenericFilterBean {

    private final TokenManager tokenManager;

    public RestOAuth2AuthorizationFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        val token = extractToken(((HttpServletRequest) request));
        if (StringUtils.hasText(token)) {
            val authentication = tokenManager.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
    private String extractToken(HttpServletRequest request) {
        val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTHENTICATION_TYPE + " ")) {
            return bearerToken.substring(AUTHENTICATION_TYPE.length() + 1);
        }
        return null;
    }
}
