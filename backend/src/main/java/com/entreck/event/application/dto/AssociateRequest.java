package com.entreck.event.application.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for associating a PointOfSale with an Event (O03).
 *
 * @param posId the point-of-sale identifier to associate
 * @param note an optional note about the association
 */
public record AssociateRequest(
    @NotNull(message = "Point of sale ID is required") Long posId,
    String note) {}
