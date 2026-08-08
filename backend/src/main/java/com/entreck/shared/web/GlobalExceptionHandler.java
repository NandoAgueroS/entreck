package com.entreck.shared.web;

import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.exception.LinkAlreadyExistsException;
import com.entreck.event.application.exception.LinkNotFoundException;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler providing consistent error responses.
 *
 * <p>Maps known domain exceptions to stable HTTP statuses and maps unexpected
 * exceptions to a correlation-ID-bearing 500 response (design section 3.4).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
   * Handles invalid query or path parameter types.
   *
   * @param ex the type mismatch exception
   * @return a 400 response with a field-level error
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String fieldName = ex.getParameter() != null && ex.getParameter().getParameterName() != null
        ? ex.getParameter().getParameterName()
        : (ex.getName() != null ? ex.getName() : "parameter");
    FieldErrorItem error = new FieldErrorItem(fieldName, "Invalid value for parameter " + fieldName);
    ErrorResponse body = new ErrorResponse("VALIDATION_ERROR", "Request validation failed", List.of(error));
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
   * Handles point-of-sale-not-found errors.
   *
   * @param ex the POS not found exception
   * @return a 404 response
   */
  @ExceptionHandler(PointOfSaleNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePointOfSaleNotFound(PointOfSaleNotFoundException ex) {
    ErrorResponse body = new ErrorResponse("POS_NOT_FOUND", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  /**
   * Handles link-not-found errors.
   *
   * @param ex the link not found exception
   * @return a 404 response
   */
  @ExceptionHandler(LinkNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleLinkNotFound(LinkNotFoundException ex) {
    ErrorResponse body = new ErrorResponse("LINK_NOT_FOUND", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  /**
   * Handles duplicate link errors.
   *
   * @param ex the link already exists exception
   * @return a 422 response
   */
  @ExceptionHandler(LinkAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleLinkAlreadyExists(LinkAlreadyExistsException ex) {
    ErrorResponse body = new ErrorResponse("LINK_ALREADY_EXISTS", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
  }

  /**
   * Handles missing static resources with a clean 404 instead of masking
   * them as 500 via the catch-all below.
   *
   * @param ex the not-found exception
   * @return a 404 response
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
    ErrorResponse body = new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  /**
   * Handles any unexpected failure with a generic 500 response and correlation ID.
   *
   * @param ex the unexpected exception
   * @return a 500 response without stack traces, with a correlation ID
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    String correlationId = UUID.randomUUID().toString();
    log.error("Unhandled exception [correlationId={}]", correlationId, ex);
    ErrorResponse body = new ErrorResponse(
        "INTERNAL_ERROR", "An unexpected error occurred", correlationId);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
