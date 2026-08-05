package com.entreck.shared.domain;

/**
 * Abstract base for all domain-level runtime exceptions.
 *
 * <p>Concrete exceptions live in the bounded contexts where the business rule
 * is enforced. The shared kernel provides a common unchecked base so the
 * interface layer can map domain errors consistently.
 */
public abstract class KernelException extends RuntimeException {

  protected KernelException(String message) {
    super(message);
  }

  protected KernelException(String message, Throwable cause) {
    super(message, cause);
  }
}
