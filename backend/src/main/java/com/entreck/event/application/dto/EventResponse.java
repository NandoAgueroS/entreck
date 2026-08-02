package com.entreck.event.application.dto;

import com.entreck.shared.domain.enums.EventCategory;
import java.time.Instant;

/**
 * General-purpose event response DTO.
 *
 * <p>Provides a complete view of the event. For list/search results, prefer
 * {@link EventSummaryResponse} to reduce payload size. For single-event detail
 * views, {@link EventDetailResponse} is semantically clearer.
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
public record EventResponse(
    Long id,
    String name,
    EventCategory category,
    Instant eventDate,
    String description,
    String status,
    Long organizerId,
    Instant createdAt,
    Instant updatedAt) {}
