package com.entreck.pos.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Contact value object")
class ContactTest {

  @Test
  @DisplayName("should create valid contact")
  void shouldCreateValidContact() {
    Contact contact = new Contact("test@example.com", "+1-555-123-4567");

    assertThat(contact.email()).isEqualTo("test@example.com");
    assertThat(contact.phone()).isEqualTo("+1-555-123-4567");
  }

  @Test
  @DisplayName("should reject blank email")
  void shouldRejectBlankEmail() {
    assertThatThrownBy(() -> new Contact("", "+1-555-123-4567"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email is required");
  }

  @Test
  @DisplayName("should reject invalid email format")
  void shouldRejectInvalidEmailFormat() {
    assertThatThrownBy(() -> new Contact("not-an-email", "+1-555-123-4567"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid email format");
  }

  @Test
  @DisplayName("should reject blank phone")
  void shouldRejectBlankPhone() {
    assertThatThrownBy(() -> new Contact("test@example.com", ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Phone is required");
  }

  @Test
  @DisplayName("should reject invalid phone format")
  void shouldRejectInvalidPhoneFormat() {
    assertThatThrownBy(() -> new Contact("test@example.com", "abc-defg"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid phone format");
  }
}
