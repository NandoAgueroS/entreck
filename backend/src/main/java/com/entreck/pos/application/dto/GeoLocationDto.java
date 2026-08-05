package com.entreck.pos.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/**
 * DTO for geographic location data.
 *
 * <p>Validates that latitude is within [-90, 90] and longitude is within
 * [-180, 180] at the API layer, providing field-level error messages
 * before the request reaches the domain layer (design §3.3).
 *
 * @param latitude the latitude in decimal degrees
 * @param longitude the longitude in decimal degrees
 */
public record GeoLocationDto(
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    double latitude,
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    double longitude) {}
