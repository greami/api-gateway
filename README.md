# API Gateway

Spring Boot gateway organized as a three-module hexagonal application:

- `domain`: framework-independent identity and role policy.
- `application`: use cases, inbound/outbound ports, and shared OpenAPI model classes.
- `adapter`: generated HTTP API interfaces, REST controllers, Feign clients/adapters, and Spring Security/JWT.

Dependencies point inward: `adapter -> application -> domain`. The application module owns the shared OpenAPI models so the domain remains independent of transport schemas.

Run the test suite and build:

```sh
mvn clean verify
```

Start the gateway:

```sh
mvn -pl adapter -am install
mvn -pl adapter spring-boot:run
```

Configure `USER_SERVICE_URL`, `CORE_SERVICE_URL`, and `SERVER_PORT` for the deployed environment. JWT settings are in `adapter/src/main/resources/application.yml`; replace development secrets before production.

Authenticate with `POST /auth/login` and send the returned bearer token in the `Authorization` header to protected endpoints.
