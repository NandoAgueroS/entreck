package com.entreck.pos.domain.valueobject;

/**
 * Immutable value object representing a physical address.
 *
 * <p>Validates that all required fields are present and non-blank.
 *
 * @param street the street address
 * @param city the city
 * @param state the state or province
 * @param zipCode the postal code
 * @param country the ISO-3166 alpha-2 country code
 */
public record Address(
    String street,
    String city,
    String state,
    String zipCode,
    String country) {

  public Address {
    if (street == null || street.isBlank()) {
      throw new IllegalArgumentException("Street is required");
    }
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City is required");
    }
    if (state == null || state.isBlank()) {
      throw new IllegalArgumentException("State is required");
    }
    if (zipCode == null || zipCode.isBlank()) {
      throw new IllegalArgumentException("Zip code is required");
    }
    if (country == null || country.isBlank()) {
      throw new IllegalArgumentException("Country is required");
    }
    if (!country.matches("^[A-Z]{2}$")) {
      throw new IllegalArgumentException("Country must be ISO-3166 alpha-2 code");
    }
  }
}
