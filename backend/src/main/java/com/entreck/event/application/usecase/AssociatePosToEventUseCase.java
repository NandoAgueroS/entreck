package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.AssociateRequest;
import com.entreck.event.application.dto.LinkedPosResponse;
import com.entreck.shared.domain.id.EventId;

/**
 * Port for the associate-POS-to-event use case (O03).
 *
 * <p>Creates a new EventPointOfSaleLink between an existing Event and an
 * existing PointOfSale. Rejects duplicate links.
 */
public interface AssociatePosToEventUseCase {

  /**
   * Associates a PointOfSale with an Event.
   *
   * @param eventId the event identifier
   * @param request the association request
   * @return the created link response
   */
  LinkedPosResponse execute(EventId eventId, AssociateRequest request);
}
