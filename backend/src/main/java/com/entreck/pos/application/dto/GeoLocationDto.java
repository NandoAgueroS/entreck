package com.entreck.pos.application.dto;

/**
 * DTO for geographic location data.
 *
 * @param latitude the latitude in decimal degrees
 * @param longitude the longitude in decimal degrees
 */
public record GeoLocationDto(double latitude, double longitude) {}
