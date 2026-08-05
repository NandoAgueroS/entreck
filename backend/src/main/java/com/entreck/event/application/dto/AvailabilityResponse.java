package com.entreck.event.application.dto;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import java.time.Instant;

/**
 * Response DTO for an updated availability status (S03).
 *
 * @param linkId the link identifier
 * @param eventId the event identifier
 * @param posId the point-of-sale identifier
 * @param availabilityStatus the new availability status
 * @param note the optional note
 * @param updatedAt the last update timestamp
 */
public record AvailabilityResponse(
    Long linkId,
    Long eventId,
    Long posId,
    AvailabilityStatus availabilityStatus,
    String note,
    Instant updatedAt) {}
