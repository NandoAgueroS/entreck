package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventResponse;
import com.entreck.event.application.dto.UpdateEventRequest;
import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.shared.domain.id.EventId;

/**
 * Port for the update-event use case (O02).
 *
 * <p>Applies partial updates (PATCH semantics) to an existing event.
 * Only non-null fields in the request are applied. The event name must
 * remain unique.
 */
public interface UpdateEventUseCase {

  /**
   * Updates an existing event.
   *
   * @param eventId the identifier of the event to update
   * @param request the update request with partial fields
   * @return the updated event response
   * @throws EventNotFoundException if the event does not exist
   * @throws DuplicateEventNameException if the new name conflicts with another event
   * @throws IllegalStateException if the event is CANCELLED and cannot be updated
   */
  EventResponse execute(EventId eventId, UpdateEventRequest request);
}
