package com.entreck.event.application.dto;

import com.entreck.event.domain.Event;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
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
    Instant updatedAt) {

  /**
   * Maps a domain Event to a detail response DTO.
   *
   * @param event the domain event
   * @return the detail response DTO
   */
  public static EventDetailResponse from(Event event) {
    return new EventDetailResponse(
        event.id().value(),
        event.name(),
        event.category(),
        event.eventDate(),
        event.description(),
        event.status().name(),
        event.organizerId().value(),
        event.createdAt(),
        event.updatedAt());
  }
}
