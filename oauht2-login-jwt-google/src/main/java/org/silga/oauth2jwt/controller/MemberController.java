package org.silga.oauth2jwt.controller;

import lombok.val;
import org.silga.oauth2jwt.dto.AdminResponse;
import org.silga.oauth2jwt.dto.UserResponse;
import org.silga.oauth2jwt.model.Member;
import org.silga.oauth2jwt.repository.MemberRepository;
import org.silga.oauth2jwt.dto.GreetResponse;
import org.silga.oauth2jwt.dto.WelcomeResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Objects;

@RestController
public class MemberController {
    private final MemberRepository repository;
    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;


    public MemberController(MemberRepository repository, OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
        this.repository = repository;
        this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
    }

    @GetMapping("/welcome")
    public WelcomeResponse welcome(HttpServletRequest request) {
        Member member = repository.findByUsername(request.getUserPrincipal().getName());
        return new WelcomeResponse(member.getName(), member.getAuthorities());
    }

    @GetMapping("/greet")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GreetResponse greet(HttpServletRequest request) {
        Member member = repository.findByUsername(request.getUserPrincipal().getName());
        return new GreetResponse("Hello " + member.getName());
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserResponse user(Principal principal) {
        val authentication = ((OAuth2AuthenticationToken) principal);
        return new UserResponse(
                Objects.requireNonNull(authentication.getPrincipal().getAttribute("email")).toString(),
                Objects.requireNonNull(authentication.getPrincipal().getAttribute("name")).toString()
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminResponse admin(Principal principal, HttpServletRequest request) {
        val authentication = ((OAuth2AuthenticationToken) principal);
        val authorizedClient = oAuth2AuthorizedClientRepository.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication,
                request
        );
        return new AdminResponse(
                Objects.requireNonNull(authentication.getPrincipal().getAttribute("email")).toString(),
                Objects.requireNonNull(authentication.getPrincipal().getAttribute("name")).toString(),
                authorizedClient.getAccessToken().getTokenValue(),
                Objects.requireNonNull(authorizedClient.getRefreshToken()).getTokenValue()
        );
    }
}
