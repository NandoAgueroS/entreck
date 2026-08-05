package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.LinkedPosResponse;
import com.entreck.shared.domain.id.EventId;
import java.util.List;

/**
 * Port for the list-event-POS use case (B04).
 *
 * <p>Returns all PointOfSale records linked to a given Event, including
 * their availability status and metadata.
 */
public interface ListEventPointOfSaleUseCase {

  /**
   * Lists all POS records linked to the given event.
   *
   * @param eventId the event identifier
   * @return a list of linked POS responses
   */
  List<LinkedPosResponse> execute(EventId eventId);
}
