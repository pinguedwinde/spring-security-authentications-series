package org.silga.oauth2jwt.config;

import org.silga.oauth2jwt.security.RestOAuth2AccessDeniedHandler;
import org.silga.oauth2jwt.security.RestOAuth2AuthenticationEntryPoint;
import org.silga.oauth2jwt.security.RestOAuth2AuthorizationFilter;
import org.silga.oauth2jwt.security.RestOAuth2AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.HashSet;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestOAuth2AuthorizationFilter restfulOAuth2AuthorizationFilter;
    private final RestOAuth2AuthenticationFilter restfulOAuth2AuthenticationFilter;
    private final RestOAuth2AuthenticationEntryPoint restOAuth2AuthenticationEntryPoint;
    private final RestOAuth2AccessDeniedHandler restOAuth2AccessDeniedHandler;

    public SecurityConfig(RestOAuth2AuthorizationFilter restfulOAuth2AuthorizationFilter, RestOAuth2AuthenticationFilter restfulOAuth2AuthenticationFilter, RestOAuth2AuthenticationEntryPoint restOAuth2AuthenticationEntryPoint, RestOAuth2AccessDeniedHandler restOAuth2AccessDeniedHandler) {
        this.restfulOAuth2AuthorizationFilter = restfulOAuth2AuthorizationFilter;
        this.restfulOAuth2AuthenticationFilter = restfulOAuth2AuthenticationFilter;
        this.restOAuth2AuthenticationEntryPoint = restOAuth2AuthenticationEntryPoint;
        this.restOAuth2AccessDeniedHandler = restOAuth2AccessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(restfulOAuth2AuthorizationFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(restfulOAuth2AuthenticationFilter, BasicAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(restOAuth2AuthenticationEntryPoint)
                .accessDeniedHandler(restOAuth2AccessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/login.html").permitAll()
                .anyRequest().authenticated();
    }
}
