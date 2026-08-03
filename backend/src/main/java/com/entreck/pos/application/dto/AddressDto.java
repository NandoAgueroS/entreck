package com.entreck.pos.application.dto;

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
    String street,
    String city,
    String state,
    String zipCode,
    String country) {}
