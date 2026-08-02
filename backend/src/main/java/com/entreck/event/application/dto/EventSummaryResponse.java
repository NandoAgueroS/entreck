package com.entreck.event.application.dto;

import com.entreck.shared.domain.enums.EventCategory;
import java.time.Instant;

/**
 * Response DTO for event summaries in list/search results (use cases B01, B02).
 *
 * <p>Lightweight representation excluding full description to reduce payload
 * size in paginated responses.
 *
 * @param id the event identifier
 * @param name the event name
 * @param category the event category
 * @param eventDate the date and time of the event
 * @param status the event status
 * @param organizerId the organizer identifier
 */
public record EventSummaryResponse(
    Long id,
    String name,
    EventCategory category,
    Instant eventDate,
    String status,
    Long organizerId) {}
