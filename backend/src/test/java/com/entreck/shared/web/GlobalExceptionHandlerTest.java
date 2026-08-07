package com.entreck.shared.web;

import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.exception.LinkAlreadyExistsException;
import com.entreck.event.application.exception.LinkNotFoundException;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  @DisplayName("should map EventNotFoundException to 404")
  void shouldMapEventNotFoundTo404() {
    var response = handler.handleEventNotFound(
        new EventNotFoundException(new EventId(1L)));

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getBody().code()).isEqualTo("EVENT_NOT_FOUND");
  }

  @Test
  @DisplayName("should map PointOfSaleNotFoundException to 404")
  void shouldMapPointOfSaleNotFoundTo404() {
    var response = handler.handlePointOfSaleNotFound(
        new PointOfSaleNotFoundException(new PointOfSaleId(1L)));

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getBody().code()).isEqualTo("POS_NOT_FOUND");
  }

  @Test
  @DisplayName("should map LinkNotFoundException to 404")
  void shouldMapLinkNotFoundTo404() {
    var response = handler.handleLinkNotFound(
        new LinkNotFoundException(new EventId(1L), new PointOfSaleId(2L)));

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getBody().code()).isEqualTo("LINK_NOT_FOUND");
  }

  @Test
  @DisplayName("should map DuplicateEventNameException to 422")
  void shouldMapDuplicateEventNameTo422() {
    var response = handler.handleDuplicateEventName(
        new DuplicateEventNameException("Test Event"));

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody().code()).isEqualTo("DUPLICATE_EVENT_NAME");
  }

  @Test
  @DisplayName("should map LinkAlreadyExistsException to 422")
  void shouldMapLinkAlreadyExistsTo422() {
    var response = handler.handleLinkAlreadyExists(
        new LinkAlreadyExistsException(new EventId(1L), new PointOfSaleId(2L)));

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody().code()).isEqualTo("LINK_ALREADY_EXISTS");
  }

  @Test
  @DisplayName("should map generic Exception to 500 with correlation ID")
  void shouldMapGenericExceptionTo500WithCorrelationId() {
    var response = handler.handleGeneric(new RuntimeException("Something broke"));

    assertThat(response.getStatusCode().value()).isEqualTo(500);
    assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
    assertThat(response.getBody().correlationId()).isNotBlank();
  }

  @Test
  @DisplayName("should map MethodArgumentTypeMismatchException to 400 VALIDATION_ERROR")
  void shouldMapTypeMismatchTo400ValidationError() {
    var response = handler.handleTypeMismatch(
        new MethodArgumentTypeMismatchException("INVALID", EventCategory.class, "category", null, null));

    assertThat(response.getStatusCode().value()).isEqualTo(400);
    assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
    assertThat(response.getBody().fieldErrors()).hasSize(1);
    assertThat(response.getBody().fieldErrors().get(0).field()).isEqualTo("category");
    assertThat(response.getBody().fieldErrors().get(0).message())
        .isEqualTo("Invalid value for parameter category");
  }

  @Test
  @DisplayName("should return empty fieldErrors for domain exceptions")
  void shouldReturnEmptyFieldErrorsForDomainExceptions() {
    var response = handler.handleEventNotFound(
        new EventNotFoundException(new EventId(1L)));

    assertThat(response.getBody().fieldErrors()).isEmpty();
  }

  @Test
  @DisplayName("should return fieldErrors for validation exceptions")
  void shouldReturnFieldErrorsForValidationExceptions() {
    var fieldErrors = List.of(new FieldErrorItem("name", "must not be blank"));
    ErrorResponse body = new ErrorResponse("VALIDATION_ERROR", "Request validation failed", fieldErrors);

    assertThat(body.fieldErrors()).hasSize(1);
    assertThat(body.fieldErrors().get(0).field()).isEqualTo("name");
  }
}
