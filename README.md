api-gateway

Modules:
- domain: core domain records and ports
- application: use-cases and mappers
- module: Spring Boot application, adapters, infra

Run:
  mvn -T 1C clean install
  mvn -pl module spring-boot:run

Notes:
- Update secrets and versions before production
- openapi-generator configured to read src/main/resources/openapi.yaml
- Authenticate with `POST /auth/login` and send `Authorization: Bearer <jwt>` to protected endpoints
