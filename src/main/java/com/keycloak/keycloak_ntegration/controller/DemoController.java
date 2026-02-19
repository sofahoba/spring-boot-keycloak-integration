package com.keycloak.keycloak_ntegration.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('role_user')")
    public String userEndpoint() {
        return "userrrrrrr";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('role_admin')")
    public String adminEndpoint() {
        return "adminnnnnnn";
    }

}