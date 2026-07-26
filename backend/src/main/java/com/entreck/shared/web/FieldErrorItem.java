package com.entreck.shared.web;

/**
 * A single field-level validation error.
 *
 * @param field the field path
 * @param message the validation message
 */
public record FieldErrorItem(String field, String message) {
}
