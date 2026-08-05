package com.entreck.event.application.exception;

import com.entreck.shared.domain.KernelException;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Thrown when an EventPointOfSaleLink cannot be found.
 *
 * <p>Mapped to HTTP 404 by the interface layer.
 */
public class LinkNotFoundException extends KernelException {

  /**
   * Constructs the exception with the event and POS identifiers.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   */
  public LinkNotFoundException(EventId eventId, PointOfSaleId posId) {
    super("Link not found for event " + eventId.value() + " and POS " + posId.value());
  }
}
