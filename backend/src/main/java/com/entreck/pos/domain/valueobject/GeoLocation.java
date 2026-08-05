package com.entreck.pos.domain.valueobject;

/**
 * Immutable value object representing geographic coordinates.
 *
 * <p>Validates that latitude is within [-90, 90] and longitude is within
 * [-180, 180].
 *
 * @param latitude the latitude in decimal degrees
 * @param longitude the longitude in decimal degrees
 */
public record GeoLocation(double latitude, double longitude) {

  public GeoLocation {
    if (latitude < -90.0 || latitude > 90.0) {
      throw new IllegalArgumentException(
          "Latitude must be between -90 and 90, got: " + latitude);
    }
    if (longitude < -180.0 || longitude > 180.0) {
      throw new IllegalArgumentException(
          "Longitude must be between -180 and 180, got: " + longitude);
    }
  }
}
