package com.entreck.event.domain;

/**
 * Lifecycle status of an {@link Event} aggregate.
 *
 * <p>State transitions follow: {@code DRAFT → PUBLISHED → CANCELLED}.
 * Once cancelled, an event cannot transition to another status.
 */
public enum EventStatus {
  /**
   * Event is being prepared and is not yet visible to buyers.
   */
  DRAFT,

  /**
   * Event is published and visible to buyers.
   */
  PUBLISHED,

  /**
   * Event has been cancelled and is no longer available.
   */
  CANCELLED
}
