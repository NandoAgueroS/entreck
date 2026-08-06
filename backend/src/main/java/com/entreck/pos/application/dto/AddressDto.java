package com.entreck.pos.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for address data.
 *
 * @param street the street address
 * @param city the city
 * @param state the state or province
 * @param zipCode the postal code
 * @param country the ISO-3166 alpha-2 country code
 */
public record AddressDto(
    @NotBlank(message = "Street is required")
    @Size(max = 200, message = "Street must not exceed 200 characters")
    String street,

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    String city,

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    String state,

    @NotBlank(message = "Zip code is required")
    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    String zipCode,

    @NotBlank(message = "Country is required")
    @Size(max = 2, message = "Country must be a 2-character ISO code")
    String country) {}
