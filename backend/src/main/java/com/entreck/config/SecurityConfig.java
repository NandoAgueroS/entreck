package com.entreck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Baseline security configuration for the Entreck backend.
 *
 * <p>All endpoints are publicly accessible in v1. The scaffold is intentionally
 * stateless so that a JWT filter can be inserted in v1.1 without rewriting
 * controllers.
 */
@Configuration
public class SecurityConfig {

  /**
   * Configures a stateless, permit-all filter chain.
   *
   * @param http the {@link HttpSecurity} builder
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception if the configuration cannot be applied
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
