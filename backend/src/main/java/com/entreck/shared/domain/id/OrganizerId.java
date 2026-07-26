package com.entreck.shared.domain.id;

/**
 * Typed identifier for an organizer that publishes events.
 *
 * <p>In v1 the identifier is accepted from the request body because the
 * authentication system is deferred to v1.1.
 *
 * @param value the numeric identifier, must not be null
 */
public record OrganizerId(Long value) {

  public OrganizerId {
    if (value == null) {
      throw new IllegalArgumentException("Organizer id is required");
    }
  }
}
