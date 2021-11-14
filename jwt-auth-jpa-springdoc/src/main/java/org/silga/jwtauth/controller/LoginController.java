package org.silga.jwtauth.controller;

import lombok.val;
import org.silga.jwtauth.dto.LoginRequest;
import org.silga.jwtauth.dto.LoginResponse;
import org.silga.jwtauth.security.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.silga.jwtauth.security.SecurityConstants.AUTHENTICATION_HEADER;
import static org.silga.jwtauth.security.SecurityConstants.AUTHENTICATION_TYPE;

@RestController
public class LoginController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginController(JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping(path = "/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse httpServletResponse) {
        val authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        val token = jwtTokenProvider.generate(authentication);
        httpServletResponse.addHeader(AUTHENTICATION_HEADER, AUTHENTICATION_TYPE + " " + token);
        return  new LoginResponse(token);
    }
}
