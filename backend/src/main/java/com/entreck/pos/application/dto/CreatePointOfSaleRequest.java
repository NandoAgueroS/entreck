package com.entreck.pos.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for registering a new point of sale (use case S01).
 *
 * @param name the point of sale name
 * @param address the physical address
 * @param location the geographic coordinates
 * @param contact the contact information
 * @param openingHours the opening hours
 * @param shopId the shop identifier
 */
public record CreatePointOfSaleRequest(
    @NotBlank(message = "Point of sale name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    String name,

    @NotNull(message = "Address is required")
    @Valid AddressDto address,

    @NotNull(message = "Location is required")
    @Valid GeoLocationDto location,

    @NotNull(message = "Contact is required")
    @Valid ContactDto contact,

    @NotNull(message = "Opening hours are required")
    @Valid OpeningHoursDto openingHours,

    @NotNull(message = "Shop id is required")
    Long shopId) {}
