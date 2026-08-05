package com.entreck.event.application.usecase;

import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Port for the dissociate-POS-from-event use case (O04).
 *
 * <p>Removes an existing EventPointOfSaleLink between an Event and a PointOfSale.
 */
public interface DissociatePosFromEventUseCase {

  /**
   * Dissociates a PointOfSale from an Event.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   */
  void execute(EventId eventId, PointOfSaleId posId);
}
