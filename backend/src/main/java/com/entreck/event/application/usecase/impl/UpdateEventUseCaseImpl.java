package com.entreck.event.application.usecase.impl;

import com.entreck.event.application.dto.EventResponse;
import com.entreck.event.application.dto.UpdateEventRequest;
import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.usecase.UpdateEventUseCase;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.id.EventId;

/**
 * Use case for updating an existing event (O02).
 *
 * <p>Applies partial updates (PATCH semantics) to an event. Only non-null
 * fields in the request are applied. The event name must remain unique.
 */
public class UpdateEventUseCaseImpl implements UpdateEventUseCase {

  private final EventRepository eventRepository;

  /**
   * Constructs the use case with the required port.
   *
   * @param eventRepository the event repository port
   */
  public UpdateEventUseCaseImpl(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /**
   * Executes the use case.
   *
   * @param eventId the identifier of the event to update
   * @param request the update request with partial fields
   * @return the updated event response
   * @throws EventNotFoundException if the event does not exist
   * @throws DuplicateEventNameException if the new name conflicts with another event
   * @throws IllegalStateException if the event is CANCELLED and cannot be updated
   */
  @Override
  public EventResponse execute(EventId eventId, UpdateEventRequest request) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventNotFoundException(eventId));

    String newName = request.name() != null ? request.name() : event.name();
    if (!newName.equals(event.name()) && eventRepository.existsByName(newName)) {
      throw new DuplicateEventNameException(newName);
    }

    event.update(
        newName,
        request.category() != null ? request.category() : event.category(),
        request.eventDate() != null ? request.eventDate() : event.eventDate(),
        request.description() != null ? request.description() : event.description());

    Event saved = eventRepository.save(event);
    return EventResponse.from(saved);
  }
}
