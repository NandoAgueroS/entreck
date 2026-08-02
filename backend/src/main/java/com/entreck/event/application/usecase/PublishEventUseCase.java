package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.CreateEventRequest;
import com.entreck.event.application.dto.EventResponse;
import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;

/**
 * Port for the publish-event use case (O01).
 *
 * <p>Creates a new event in DRAFT status and immediately publishes it.
 * The event name must be unique across the system.
 */
public interface PublishEventUseCase {

  /**
   * Publishes a new event.
   *
   * @param request the create event request
   * @param organizerId the organizer creating the event
   * @param eventId the identifier to assign to the new event
   * @return the published event response
   * @throws DuplicateEventNameException if the event name already exists
   */
  EventResponse execute(CreateEventRequest request, OrganizerId organizerId, EventId eventId);
}
