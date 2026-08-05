package com.entreck;

import com.entreck.shared.testing.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test that boots the full Spring context against a real PostgreSQL
 * database and verifies the Actuator health endpoint.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class BackendApplicationSmokeTest extends BaseIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void actuatorHealthReturnsUp() {
    ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("\"status\":\"UP\"");
  }
}
