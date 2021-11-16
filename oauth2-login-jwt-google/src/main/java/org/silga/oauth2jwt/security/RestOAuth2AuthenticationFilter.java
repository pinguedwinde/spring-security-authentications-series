package org.silga.oauth2jwt.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.silga.oauth2jwt.dto.TokenResponse;
import org.silga.oauth2jwt.model.Member;
import org.silga.oauth2jwt.repository.MemberRepository;
import org.silga.oauth2jwt.service.MemberUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.silga.oauth2jwt.security.SecurityConstants.*;

@Component
public class RestOAuth2AuthenticationFilter extends GenericFilterBean {

    private final Logger LOGGER = LoggerFactory.getLogger(RestOAuth2AuthenticationFilter.class);

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final TokenManager tokenManager;
    private final MemberUserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    private AuthenticationManager authenticationManager;
    private final DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final AntPathRequestMatcher requestMatcher ;

    public RestOAuth2AuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository, TokenManager tokenManager, MemberUserDetailsService userDetailsService, MemberRepository memberRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
        this.memberRepository = memberRepository;
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, BASE_URI);
        //this.requestMatcher = new AntPathRequestMatcher("/auth/google", HttpMethod.POST.name());
        this.requestMatcher = new AntPathRequestMatcher(String.format("/%s/{%s}",BASE_URI, REGISTRATION_ID_URI_VARIABLE_NAME), HttpMethod.POST.name());
        init();
    }

    private void init() {
        val accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        val userService = new OidcUserService();
        val authenticationProvider = new OidcAuthorizationCodeAuthenticationProvider(accessTokenResponseClient, userService);
        authenticationManager = new ProviderManager(authenticationProvider);
        authorizationRequestResolver.setAuthorizationRequestCustomizer(builder -> {
            builder.redirectUri(REDIRECT_URI)
                    .additionalParameters(additionalParameters -> additionalParameters.remove(NONCE_PARAMETER_NAME))
                    .attributes(attributes -> attributes.remove(NONCE_PARAMETER_NAME));
        });
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        val request = ((HttpServletRequest) servletRequest);
        val response = ((HttpServletResponse) servletResponse);
        if (!requireAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            val authentication = authenticate(request, response);
            successfulAuthentication(response, authentication);
        } catch (Exception e) {
            //e.printStackTrace();
            unsuccessfulAuthentication(response, e);
        }
    }

    private boolean requireAuthentication(HttpServletRequest request){
        // check whether the requested URI is /auth/{registrationId}
        return requestMatcher.matches(request);
    }

    private OAuth2AuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response){
        val code = readCode(request);
        if(code == null)
            throw new OAuth2AuthenticationException(new OAuth2Error("authentication_code_missing"));
        val registrationId = requestMatcher.matcher(request).getVariables().get(REGISTRATION_ID_URI_VARIABLE_NAME);
        if(registrationId == null)
            throw new OAuth2AuthenticationException(new OAuth2Error("client_registration_not_found"));
        val clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if(clientRegistration == null)
            throw new OAuth2AuthenticationException(new OAuth2Error("client_registration_not_found"));
        val authorizationRequest = authorizationRequestResolver.resolve(request, registrationId);
        val authorizationResponse = OAuth2AuthorizationResponse
                .success(code)
                .redirectUri(REDIRECT_URI)
                .state(authorizationRequest.getState())
                .build();

        val authenticationRequest = new OAuth2LoginAuthenticationToken(
                clientRegistration,
                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse)
        );
        val authenticationResult = ((OAuth2LoginAuthenticationToken) authenticationManager.authenticate(authenticationRequest));
        val username = authenticationResult.getPrincipal().getAttributes().get("email").toString();
        val user = loadUser(username) ;
        if(user == null)
            createUser(authenticationResult);
        assert user != null;
        val authorities = mergeAuthorities(authenticationResult, user);
        val oauth2Authentication = new OAuth2AuthenticationToken(
                authenticationResult.getPrincipal(),
                authorities,
                authenticationResult.getClientRegistration().getRegistrationId()
        );
        val authorizedClient = new OAuth2AuthorizedClient(
                authenticationResult.getClientRegistration(),
                oauth2Authentication.getName(),
                authenticationResult.getAccessToken(),
                authenticationResult.getRefreshToken()
        );

        authorizedClientRepository.saveAuthorizedClient(authorizedClient, oauth2Authentication, request, response);
        return oauth2Authentication;
    }

    private String readCode(HttpServletRequest request) {
        Map<String, Object> authRequest = null;
        try {
            authRequest = new ObjectMapper().readValue(request.getReader(), new TypeReference<>() {
            });
        }catch (IOException e){
            LOGGER.error(e.getMessage());
        }
        assert authRequest != null;
        return authRequest.get("code").toString();
    }

    private UserDetails loadUser(String username){
        try {
            return userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }

    private UserDetails createUser(OAuth2LoginAuthenticationToken authentication) {
        val attributes = authentication.getPrincipal().getAttributes();
        val username = attributes.get("email").toString();
        val member = new Member(
                username,
                authentication.getClientRegistration().getRegistrationId(),
                attributes.get("name").toString(),
                List.of("ROLE_USER")
        );
        memberRepository.save(member);
        return userDetailsService.loadUserByUsername(username);
    }

    private Collection<GrantedAuthority> mergeAuthorities(OAuth2LoginAuthenticationToken authentication, UserDetails user){
        val authorities = new HashSet<GrantedAuthority>();
        authorities.addAll(authentication.getAuthorities());
        authorities.addAll(user.getAuthorities());
        return authorities;
    }

    private void successfulAuthentication(HttpServletResponse response, OAuth2AuthenticationToken authentication) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        val token = tokenManager.generate(authentication);
        tokenManager.setAuthentication(token, authentication);
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().println(new ObjectMapper().writeValueAsString(new TokenResponse(token)));
    }

    private void unsuccessfulAuthentication(HttpServletResponse response, Exception exception) throws IOException {
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }

}
