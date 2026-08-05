package com.entreck.pos.application.dto;

import java.time.Instant;

/**
 * Response DTO for full point of sale detail (use case B06).
 *
 * <p>Includes all fields including address, location, contact, and opening hours.
 *
 * @param id the point of sale identifier
 * @param name the point of sale name
 * @param address the physical address
 * @param location the geographic coordinates
 * @param contact the contact information
 * @param openingHours the opening hours
 * @param status the POS status
 * @param shopId the shop identifier
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record PointOfSaleDetailResponse(
    Long id,
    String name,
    AddressDto address,
    GeoLocationDto location,
    ContactDto contact,
    OpeningHoursDto openingHours,
    String status,
    Long shopId,
    Instant createdAt,
    Instant updatedAt) {}
