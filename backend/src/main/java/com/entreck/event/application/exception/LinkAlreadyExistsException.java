package com.entreck.event.application.exception;

import com.entreck.shared.domain.KernelException;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Thrown when attempting to create a duplicate EventPointOfSaleLink.
 *
 * <p>Mapped to HTTP 422 by the interface layer.
 */
public class LinkAlreadyExistsException extends KernelException {

  /**
   * Constructs the exception with the conflicting event and POS identifiers.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   */
  public LinkAlreadyExistsException(EventId eventId, PointOfSaleId posId) {
    super("Link already exists for event " + eventId.value() + " and POS " + posId.value());
  }
}
