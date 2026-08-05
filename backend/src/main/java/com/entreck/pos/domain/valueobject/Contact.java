package com.entreck.pos.domain.valueobject;

/**
 * Immutable value object representing contact information.
 *
 * <p>Validates email format (RFC-5322-lite) and phone format (digits, spaces,
 * dashes, parentheses, optional leading +).
 *
 * @param email the contact email address
 * @param phone the contact phone number
 */
public record Contact(String email, String phone) {

  public Contact {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email is required");
    }
    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
      throw new IllegalArgumentException("Invalid email format: " + email);
    }
    if (phone == null || phone.isBlank()) {
      throw new IllegalArgumentException("Phone is required");
    }
    if (!phone.matches("^\\+?[0-9\\s\\-()]+$")) {
      throw new IllegalArgumentException("Invalid phone format: " + phone);
    }
  }
}
