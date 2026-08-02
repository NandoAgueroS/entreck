package com.entreck.event.application.dto;

import com.entreck.shared.domain.enums.EventCategory;
import java.time.Instant;

/**
 * Response DTO for full event detail (use case B03).
 *
 * <p>Includes all fields including the full description, suitable for
 * single-event detail views.
 *
 * @param id the event identifier
 * @param name the event name
 * @param category the event category
 * @param eventDate the date and time of the event
 * @param description the event description
 * @param status the event status
 * @param organizerId the organizer identifier
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record EventDetailResponse(
    Long id,
    String name,
    EventCategory category,
    Instant eventDate,
    String description,
    String status,
    Long organizerId,
    Instant createdAt,
    Instant updatedAt) {}
