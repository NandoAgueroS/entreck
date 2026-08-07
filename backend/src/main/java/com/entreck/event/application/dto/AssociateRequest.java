package com.entreck.event.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for associating a PointOfSale with an Event (O03).
 *
 * @param posId the point-of-sale identifier to associate
 * @param note an optional note about the association
 */
public record AssociateRequest(
    @NotNull(message = "Point of sale ID is required") Long posId,
    @Size(max = 140, message = "Note must not exceed 140 characters") String note) {}
