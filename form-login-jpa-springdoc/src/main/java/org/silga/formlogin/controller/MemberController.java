package org.silga.formlogin.controller;

import lombok.val;
import org.silga.formlogin.dto.GreetResponse;
import org.silga.formlogin.dto.WelcomeResponse;
import org.silga.formlogin.model.Member;
import org.silga.formlogin.repository.MemberRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/greet")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GreetResponse greet(HttpServletRequest request) {
        Member member = repository.findByUsername(request.getUserPrincipal().getName());
        return new GreetResponse("Hello " + member.getName());
    }
}
