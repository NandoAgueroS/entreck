package com.entreck.pos.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Address value object")
class AddressTest {

  @Test
  @DisplayName("should create valid address")
  void shouldCreateValidAddress() {
    Address address = new Address("123 Main St", "Springfield", "IL", "62701", "US");

    assertThat(address.street()).isEqualTo("123 Main St");
    assertThat(address.city()).isEqualTo("Springfield");
    assertThat(address.country()).isEqualTo("US");
  }

  @Test
  @DisplayName("should reject blank street")
  void shouldRejectBlankStreet() {
    assertThatThrownBy(() -> new Address("", "City", "State", "12345", "US"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Street is required");
  }

  @Test
  @DisplayName("should reject null city")
  void shouldRejectNullCity() {
    assertThatThrownBy(() -> new Address("Street", null, "State", "12345", "US"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("City is required");
  }

  @Test
  @DisplayName("should reject invalid country code")
  void shouldRejectInvalidCountryCode() {
    assertThatThrownBy(() -> new Address("Street", "City", "State", "12345", "USA"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ISO-3166");
  }

  @Test
  @DisplayName("should reject lowercase country code")
  void shouldRejectLowercaseCountryCode() {
    assertThatThrownBy(() -> new Address("Street", "City", "State", "12345", "us"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ISO-3166");
  }
}
