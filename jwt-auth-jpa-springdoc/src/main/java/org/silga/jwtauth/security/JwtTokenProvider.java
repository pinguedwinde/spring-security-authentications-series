package org.silga.jwtauth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.val;
import org.jboss.logging.Logger;
import org.silga.jwtauth.config.AppPropertiesConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import static org.silga.jwtauth.security.SecurityConstants.CLAIM_AUTHORITIES;

@Component
public class JwtTokenProvider {

    private final Logger LOGGER = Logger.getLogger(JwtTokenProvider.class);
    private final AppPropertiesConfig appPropertiesConfig;
    private byte[] key;

    public JwtTokenProvider(AppPropertiesConfig appPropertiesConfig) {
        this.appPropertiesConfig = appPropertiesConfig;
        this.key = Base64.getDecoder().decode(appPropertiesConfig.getSecret());
    }

    public String generate(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        val expiration = new Date(System.currentTimeMillis() + (60 * 60 * 1000));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(CLAIM_AUTHORITIES, authorities)
                .signWith(SignatureAlgorithm.HS512, key)
                .setExpiration(expiration)
                .compact();
    }

    public Authentication toAuthentication(String token) {
        val jwtParser = Jwts.parser().setSigningKey(key);
        val claims = jwtParser.parseClaimsJws(token).getBody();
        val authorities = Arrays.stream(claims.get(CLAIM_AUTHORITIES).toString().split(","))
                .map(authority -> (GrantedAuthority) () -> authority)
                .collect(Collectors.toList());
        val user = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    public Boolean validate(String token) {
        val jwtParser = Jwts.parser().setSigningKey(key);
        try {
            jwtParser.parse(token);
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }
}
