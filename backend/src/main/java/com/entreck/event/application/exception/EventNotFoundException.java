package com.entreck.event.application.exception;

import com.entreck.shared.domain.KernelException;
import com.entreck.shared.domain.id.EventId;

/**
 * Thrown when an Event cannot be found by its identifier.
 *
 * <p>Mapped to HTTP 404 by the interface layer.
 */
public class EventNotFoundException extends KernelException {

  /**
   * Constructs the exception with the event identifier.
   *
   * @param id the event identifier that was not found
   */
  public EventNotFoundException(EventId id) {
    super("Event not found: " + id.value());
  }
}
