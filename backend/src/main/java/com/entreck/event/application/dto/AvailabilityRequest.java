package com.entreck.event.application.dto;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating the availability of an EventPointOfSaleLink (S03).
 *
 * @param availabilityStatus the new availability status
 * @param note an optional note describing the availability change
 */
public record AvailabilityRequest(
    @NotNull(message = "Availability status is required") AvailabilityStatus availabilityStatus,
    @Size(max = 140, message = "Note must not exceed 140 characters") String note) {}
