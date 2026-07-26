package com.entreck.shared.web;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler providing consistent error responses.
 *
 * <p>Validation and generic exception handling are handled here. Domain-level
 * error mappings (404/422 for Event, PointOfSale, and Link exceptions) will be
 * added in the final cross-cutting slice.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles bean validation failures.
   *
   * @param ex the validation exception
   * @return a 400 response with field-level errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    List<FieldErrorItem> errors = ex.getBindingResult().getFieldErrors().stream()
      .map(fieldError -> new FieldErrorItem(fieldError.getField(), fieldError.getDefaultMessage()))
      .toList();

    ErrorResponse body = new ErrorResponse("VALIDATION_ERROR", "Request validation failed", errors);
    return ResponseEntity.badRequest().body(body);
  }

  /**
   * Handles any unexpected failure with a generic 500 response.
   *
   * @param ex the unexpected exception
   * @return a 500 response without stack traces
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    ErrorResponse body = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
