package org.silga.formlogin.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.silga.formlogin.dto.LoginRequest;
import org.silga.formlogin.error.NotImplementedError;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void login(@RequestBody LoginRequest request) {
        throw new NotImplementedError("/login should not be called");
    }
}
