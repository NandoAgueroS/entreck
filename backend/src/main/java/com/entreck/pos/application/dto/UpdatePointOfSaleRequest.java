package com.entreck.pos.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing point of sale (use case S02).
 *
 * <p>All fields are optional — only non-null fields are updated (PATCH
 * semantics per ADR-13).
 *
 * @param name the new name, or null to leave unchanged
 * @param address the new address, or null to leave unchanged
 * @param location the new geo location, or null to leave unchanged
 * @param contact the new contact, or null to leave unchanged
 * @param openingHours the new opening hours, or null to leave unchanged
 */
public record UpdatePointOfSaleRequest(
    @Size(max = 200, message = "Name must not exceed 200 characters")
    String name,

    @Valid AddressDto address,

    @Valid GeoLocationDto location,

    @Valid ContactDto contact,

    @Valid OpeningHoursDto openingHours) {}
