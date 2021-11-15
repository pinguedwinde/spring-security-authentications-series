package org.silga.oauth2login.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Principal getPrincipal(Principal principal){
        return principal;
    }
}
