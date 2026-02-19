package com.keycloak.keycloak_ntegration.service;

import com.keycloak.keycloak_ntegration.dto.LoginRequest;
import com.keycloak.keycloak_ntegration.dto.UserRegistrationRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakService {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issueUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public KeycloakService() {
        this.restTemplate = new RestTemplate();
    }

    public Object login(LoginRequest request) {
        String tokenEndpoint = issueUrl + "/protocol/openid-connect/token";

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("client_id", clientId);
        data.add("client_secret", clientSecret);
        data.add("grant_type", "password");
        data.add("username", request.username());
        data.add("password", request.password());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(data, headers);

        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(tokenEndpoint, entity, Object.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public String registerUser(UserRegistrationRecord userRecord) {
        String adminToken = getAdminToken();

        /* - issuerUrl is http://localhost:9090/realms/keycloakIntegration
           - users endpoint is http://localhost:9090/admin/realms/keycloakIntegration/users
        */

        String realmName = issueUrl.substring(issueUrl.lastIndexOf("/") + 1);
        String baseUrl = issueUrl.substring(0, issueUrl.indexOf("/realms"));
        String usersEndpoint = baseUrl + "/admin/realms/" + realmName + "/users";

        Map<String, Object> userBody = new HashMap<>();
        userBody.put("username", userRecord.username());
        userBody.put("email", userRecord.email());
        userBody.put("enabled", true);
        userBody.put("firstName", userRecord.firstName());
        userBody.put("lastName", userRecord.lastName());

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", userRecord.password());
        credential.put("temporary", false);
        userBody.put("credentials", List.of(credential));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(usersEndpoint, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return "User registered successfully";
            }
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
        return "Registration failed";
    }


    private String getAdminToken() {
        String tokenEndpoint = issueUrl + "/protocol/openid-connect/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        try {
            Map body = restTemplate.postForObject(tokenEndpoint, entity, Map.class);
            return (String) body.get("access_token");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("KEYCLOAK ERROR: " + e.getResponseBodyAsString());
            throw new RuntimeException("Keycloak denied access: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get Admin Token: " + e.getMessage());
        }
    }
}