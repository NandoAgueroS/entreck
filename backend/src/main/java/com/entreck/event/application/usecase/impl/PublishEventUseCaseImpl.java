package com.entreck.event.application.usecase.impl;

import com.entreck.event.application.dto.CreateEventRequest;
import com.entreck.event.application.dto.EventResponse;
import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.usecase.PublishEventUseCase;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventStatus;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import java.time.Instant;

/**
 * Use case for creating and publishing a new event (O01).
 *
 * <p>Creates an Event in DRAFT status, then immediately publishes it. The
 * event name must be unique; a {@link DuplicateEventNameException} is raised
 * if a conflict is detected.
 */
public class PublishEventUseCaseImpl implements PublishEventUseCase {

  private final EventRepository eventRepository;

  /**
   * Constructs the use case with the required port.
   *
   * @param eventRepository the event repository port
   */
  public PublishEventUseCaseImpl(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /**
   * Executes the use case.
   *
   * @param request the create event request
   * @param organizerId the organizer creating the event
   * @param eventId the identifier to assign to the new event
   * @return the published event response
   * @throws DuplicateEventNameException if the event name already exists
   */
  @Override
  public EventResponse execute(CreateEventRequest request, OrganizerId organizerId, EventId eventId) {
    if (eventRepository.existsByName(request.name())) {
      throw new DuplicateEventNameException(request.name());
    }

    Instant now = Instant.now();
    Event event = new Event(
        eventId,
        request.name(),
        request.category(),
        request.eventDate(),
        request.description(),
        organizerId,
        EventStatus.DRAFT,
        now,
        now);

    event.publish();
    Event saved = eventRepository.save(event);
    return EventResponse.from(saved);
  }
}
