package com.entreck.shared.web;

import java.util.List;

/**
 * Consistent error response returned by the global exception handler.
 *
 * @param code the machine-readable error code
 * @param message the human-readable error message
 * @param fieldErrors the optional list of field-level validation errors
 */
public record ErrorResponse(String code, String message, List<FieldErrorItem> fieldErrors) {

  public ErrorResponse(String code, String message) {
    this(code, message, List.of());
  }
}
