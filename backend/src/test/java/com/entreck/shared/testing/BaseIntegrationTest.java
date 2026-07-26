package com.entreck.shared.testing;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that need a real PostgreSQL database.
 *
 * <p>Subclasses inherit a single, statically reused {@link PostgreSQLContainer}
 * running the same {@code postgres:16} image used in the local Docker Compose
 * setup. The datasource properties are injected into the Spring context via
 * {@link DynamicPropertySource}.
 */
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
    .withDatabaseName("entreck")
    .withUsername("test")
    .withPassword("test");

  @DynamicPropertySource
  static void datasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }
}
