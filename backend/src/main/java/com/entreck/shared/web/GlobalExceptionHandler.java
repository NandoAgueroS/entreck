package com.entreck.shared.web;

import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.exception.EventNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler providing consistent error responses.
 *
 * <p>Validation and domain-level error mappings are handled here. Event
 * exceptions are mapped in this slice; POS and link exceptions will be added
 * in the final cross-cutting slice (slice 6).
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
   * Handles event-not-found errors.
   *
   * @param ex the event not found exception
   * @return a 404 response
   */
  @ExceptionHandler(EventNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEventNotFound(EventNotFoundException ex) {
    ErrorResponse body = new ErrorResponse("EVENT_NOT_FOUND", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  /**
   * Handles duplicate event name errors.
   *
   * @param ex the duplicate event name exception
   * @return a 422 response
   */
  @ExceptionHandler(DuplicateEventNameException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEventName(DuplicateEventNameException ex) {
    ErrorResponse body = new ErrorResponse("DUPLICATE_EVENT_NAME", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
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
