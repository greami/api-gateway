This module contains the Spring Boot application.

- application.yml configures H2 and JWT secret
- PrenotazioneAPI.yaml is used by openapi-generator-maven-plugin to create API stubs
- schema.sql and data.sql are executed on startup (spring.sql.init.mode=always)

Next steps:
- adjust spring.boot.version in parent pom to latest if needed
- run `mvn -pl module clean spring-boot:run` to start
