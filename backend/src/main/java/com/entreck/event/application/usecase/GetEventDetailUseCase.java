package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventDetailResponse;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.shared.domain.id.EventId;

/**
 * Port for the get-event-detail use case (B03).
 *
 * <p>Returns a complete view of an event including all fields. Raises
 * {@link EventNotFoundException} if the event does not exist.
 */
public interface GetEventDetailUseCase {

  /**
   * Retrieves full detail for an event.
   *
   * @param eventId the identifier of the event to retrieve
   * @return the full event detail response
   * @throws EventNotFoundException if the event does not exist
   */
  EventDetailResponse execute(EventId eventId);
}
