package org.silga.jwtauth.controller;

import org.silga.jwtauth.dto.GreetResponse;
import org.silga.jwtauth.dto.WelcomeResponse;
import org.silga.jwtauth.model.Member;
import org.silga.jwtauth.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class MemberController {
    private final MemberRepository repository;
    public MemberController(MemberRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/welcome")
    public WelcomeResponse welcome(HttpServletRequest request) {
        Member member = repository.findByUsername(request.getUserPrincipal().getName());
        return new WelcomeResponse(member.getName(), member.getAuthorities());
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> getALlMembers() {
        List<Member> members = repository.findAll();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/greet")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GreetResponse greet(HttpServletRequest request) {
        Member member = repository.findByUsername(request.getUserPrincipal().getName());
        return new GreetResponse("Hello " + member.getName());
    }
}
