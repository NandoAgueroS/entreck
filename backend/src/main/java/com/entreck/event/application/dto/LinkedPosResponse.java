package com.entreck.event.application.dto;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import java.time.Instant;

/**
 * Response DTO representing a PointOfSale linked to an Event (B04).
 *
 * @param linkId the link identifier
 * @param eventId the event identifier
 * @param posId the point-of-sale identifier
 * @param posName the point-of-sale name
 * @param availabilityStatus the current availability status
 * @param note the optional availability note
 * @param updatedAt the last update timestamp
 */
public record LinkedPosResponse(
    Long linkId,
    Long eventId,
    Long posId,
    String posName,
    AvailabilityStatus availabilityStatus,
    String note,
    Instant updatedAt) {}
