# Spring Boot + Keycloak Integration

<div align="center">
    <img src="https://miro.medium.com/1*xnkH1MjqmcAEuoAeTvlpYw.png" alt="Spring Boot + Keycloak" width="100%">
</div>

## 📖 The Main Idea

This project demonstrates a robust implementation of **Identity and Access Management (IAM)** using **Spring Boot 4** and **Keycloak**.

Instead of handling user passwords and sessions within the Spring application, we offload authentication to Keycloak (an OAuth2/OIDC provider). The Spring Boot application acts as a **Resource Server** that verifies JWT tokens and enforces **Role-Based Access Control (RBAC)**.

### Key Features
*   **User Registration:** A REST endpoint that creates users directly in Keycloak using a Service Account (Client Credentials Flow).
*   **User Login:** Exchanges credentials for an Access Token using the Direct Access Grants flow.
*   **RBAC (Role-Based Access Control):** Protects endpoints based on Client Roles (`role_user`, `role_admin`).
*   **Custom JWT Converter:** Maps Keycloak's nested `resource_access` claims to Spring Security Authorities.

---

## 🛠 Tech Stack
*   **Java 21**
*   **Spring Boot 4.0.2** (Security, Web, OAuth2 Resource Server,lombok)
*   **Keycloak** (running via Docker)
*   **Docker & Docker Compose**

---

## 🚀 How to Run the Application

Follow these steps to go from cloning the repo to a running system.

### 1. Clone the Repository
```bash
git clone https://github.com/sofahoba/spring-boot-keycloak-integration.git
```
### 2.Start Keycloak
```bash
docker-compose up -d
```
3. Configure Keycloak (Crucial Step)
   
    - Login: Go to http://localhost:9090 (User: admin, Pass: password).
    - Create Realm: Create a new realm named keycloakIntegration.
    - Create Client: Create a client named demo.
    - Configure Client (demo):
        Capability Config: Turn ON Client Authentication (Confidential).
        Authentication Flow: Enable Standard Flow, Direct Access Grants, and Service Accounts Roles.
    - Get Secret: Go to the Credentials tab and copy the Client Secret.
    - Add Permissions:
        Go to Service Account Roles tab -> Assign Role.
        Filter by Clients -> Select realm-management -> Assign manage-users.
    - Create Roles:
        Go to Client Roles tab.
        Create role_user and role_admin.
    - Set Default Role:
        Go to Realm Roles -> default-roles-keycloakIntegration.
        Assign Role -> Filter by Clients -> Select role_user.
      
### 4. Configure Application Secret
Open src/main/resources/application.properties and paste your client secret.
  ```bash
  # ===============================
# Spring Security OAuth2 Resource Server
# ===============================

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/keycloakIntegration
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9090/realms/keycloakIntegration/protocol/openid-connect/certs
keycloak.client-id=put {your client id }
keycloak.client-secret={put your client secret}
# ===============================
# Logging
# ===============================

logging.level.org.springframework.security=DEBUG

# ===============================
# Custom JWT Properties
# ===============================

jwt.auth.converter.resource-id={your client id}
jwt.auth.converter.principle-attribute=koko
```
### 5. Run the Application

You can run the app using Maven wrapper or your IDE.

```bash

./mvnw spring-boot:run
```
The application will start on http://localhost:8080.


### API ENDPOINTS
1. Register a User

 ```
    POST /auth/register
    Description: Creates a new user in Keycloak with the default role_user.
```
Request Body:
```json

{
  "username": "jdoe",
  "email": "jdoe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "password123"
}
```

2. Login

```
    POST /auth/login
```
  Description: Authenticates the user and returns JWT tokens (Access & Refresh).

Request Body:

```json

{
  "username": "jdoe",
  "password": "password123"
}
```

🔒 Protected Endpoints (RBAC)

Requires Header: Authorization: Bearer <access_token>
3. User Endpoint

    GET /api/user
    Access: Requires role_user (Assigned by default on registration).
    Response: userrrrrrr

4. Admin Endpoint

        GET /api/admin
        Access: Requires role_admin.
        Note: To test this, you must manually assign role_admin to a user in the Keycloak Admin Console.
        Response: adminnnnnnn



