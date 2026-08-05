package com.entreck.shared.domain.id;

/**
 * Typed identifier for an {@link com.entreck.event.domain.Event} aggregate.
 *
 * <p>Wrapping the numeric identifier prevents accidental substitution with
 * identifiers from other aggregates.
 *
 * @param value the numeric identifier, must not be null
 */
public record EventId(Long value) {

  public EventId {
    if (value == null) {
      throw new IllegalArgumentException("Event id is required");
    }
  }
}
