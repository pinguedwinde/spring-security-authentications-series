package org.silga.oauth2login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.HashSet;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/success.html");
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            var mappedAuthorities  =  new HashSet<GrantedAuthority>();
            authorities.forEach(grantedAuthority -> {
                mappedAuthorities.add(grantedAuthority);
                if(grantedAuthority instanceof OidcUserAuthority){
                    var oidcUserAuthority = ((OidcUserAuthority) grantedAuthority);
                    var email = oidcUserAuthority.getAttributes().get("email").toString();
                    if(email.equals("sliga.brice@gmail.com")){
                        mappedAuthorities.add(
                                new OidcUserAuthority("ROLE_ADMIN", oidcUserAuthority.getIdToken(), oidcUserAuthority.getUserInfo())
                        );
                    }
                }
            });
            return mappedAuthorities;
        };
    }
}
