package com.entreck.event.application.dto;

import com.entreck.event.domain.Event;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
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
    Long organizerId) {

  /**
   * Maps a domain Event to a summary response DTO.
   *
   * @param event the domain event
   * @return the summary response DTO
   */
  public static EventSummaryResponse from(Event event) {
    return new EventSummaryResponse(
        event.id().value(),
        event.name(),
        event.category(),
        event.eventDate(),
        event.status().name(),
        event.organizerId().value());
  }
}
