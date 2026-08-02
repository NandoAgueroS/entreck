package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventDetailResponse;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.id.EventId;

/**
 * Use case for retrieving full event detail (B03).
 *
 * <p>Returns a complete view of the event including all fields. Raises
 * {@link EventNotFoundException} if the event does not exist.
 */
public class GetEventDetailUseCase {

  private final EventRepository eventRepository;

  /**
   * Constructs the use case with the required port.
   *
   * @param eventRepository the event repository port
   */
  public GetEventDetailUseCase(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /**
   * Executes the use case.
   *
   * @param eventId the identifier of the event to retrieve
   * @return the full event detail response
   * @throws EventNotFoundException if the event does not exist
   */
  public EventDetailResponse execute(EventId eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventNotFoundException(eventId));
    return EventDetailResponse.from(event);
  }
}
