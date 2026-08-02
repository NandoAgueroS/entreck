package com.entreck.event.application.exception;

import com.entreck.shared.domain.KernelException;

/**
 * Thrown when an event name conflicts with an existing event.
 *
 * <p>Event names must be unique. This exception is raised when a create or
 * update operation attempts to use a name that already exists.
 *
 * <p>Mapped to HTTP 422 by the interface layer.
 */
public class DuplicateEventNameException extends KernelException {

  /**
   * Constructs the exception with the conflicting event name.
   *
   * @param name the event name that already exists
   */
  public DuplicateEventNameException(String name) {
    super("Event name already exists: " + name);
  }
}
