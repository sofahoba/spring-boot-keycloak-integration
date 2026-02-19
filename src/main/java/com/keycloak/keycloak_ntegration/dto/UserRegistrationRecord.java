package com.keycloak.keycloak_ntegration.dto;

public record UserRegistrationRecord(String username, String email, String firstName, String lastName, String password) {}
