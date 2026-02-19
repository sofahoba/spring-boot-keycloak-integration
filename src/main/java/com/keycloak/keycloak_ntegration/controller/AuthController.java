package com.keycloak.keycloak_ntegration.controller;


import com.keycloak.keycloak_ntegration.dto.LoginRequest;
import com.keycloak.keycloak_ntegration.dto.UserRegistrationRecord;
import com.keycloak.keycloak_ntegration.service.KeycloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakService keycloakService;

    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(keycloakService.login(loginRequest));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRecord userRegistrationRecord) {
        try {
            String result = keycloakService.registerUser(userRegistrationRecord);
            return ResponseEntity.status(201).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error registering user: " + e.getMessage());
        }
    }
}
