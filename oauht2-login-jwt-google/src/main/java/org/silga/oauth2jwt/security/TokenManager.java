package org.silga.oauth2jwt.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.val;
import org.jboss.logging.Logger;
import org.silga.oauth2jwt.config.AppPropertiesConfig;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.silga.oauth2jwt.security.SecurityConstants.*;

@Component
public class TokenManager {

    private final Logger LOGGER = Logger.getLogger(TokenManager.class);
    private final AppPropertiesConfig appPropertiesConfig;
    private byte[] key;
    private final CacheManager cacheManager;
    private Cache cache;

    public TokenManager(AppPropertiesConfig appPropertiesConfig, CacheManager cacheManager) {
        this.appPropertiesConfig = appPropertiesConfig;
        this.key = Base64.getDecoder().decode(appPropertiesConfig.getSecret());
        this.cacheManager = cacheManager;
        this.cache = cacheManager.getCache("tokenManager");
    }

    public OAuth2AuthenticationToken getAuthentication(String token) {
        if (validate(token)) {
            return ((OAuth2AuthenticationToken) Objects.requireNonNull(cache.get(token)).get());
        } else {
            cache.evict(token);
            return null;
        }
    }

    public void setAuthentication(String token, OAuth2AuthenticationToken authentication) {
        cache.put(token, authentication);
    }

    public String generate(OAuth2AuthenticationToken authentication) {
        val subject = authentication.getName();
        val name = authentication.getPrincipal().getAttributes().get("name").toString();
        val email =  authentication.getPrincipal().getAttributes().get("email").toString();
        val authorities =  authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        val expiration = new Date(System.currentTimeMillis() + (60 * 60 * 1000));

        return Jwts.builder()
                .setSubject(subject)
                .claim(CLAIM_AUTHORITIES, authorities)
                .claim(CLAIM_NAME, name)
                .claim(CLAIM_EMAIL, email)
                .signWith(SignatureAlgorithm.HS512, key)
                .setExpiration(expiration)
                .compact();
    }

    private Boolean validate(String token) {
        try {
            val jwtParser = Jwts.parser().setSigningKey(key);
            jwtParser.parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
