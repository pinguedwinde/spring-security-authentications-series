package org.silga.jwtauth.security;

import lombok.val;
import org.springframework.security.core.Authentication;
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

import static org.silga.jwtauth.security.SecurityConstants.AUTHENTICATION_HEADER;
import static org.silga.jwtauth.security.SecurityConstants.AUTHENTICATION_TYPE;

@Component
public class JwtAuthFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        val token = extractToken((HttpServletRequest)request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validate(token)) {
            SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.toAuthentication(token));
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        val bearerToken = request.getHeader(AUTHENTICATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTHENTICATION_TYPE)) {
            return bearerToken.substring(AUTHENTICATION_TYPE.length() + 1);
        }
        return "";
    }
}
