package com.entreck.pos.application.exception;

import com.entreck.shared.domain.KernelException;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Thrown when a PointOfSale cannot be found by its identifier.
 *
 * <p>Mapped to HTTP 404 by the interface layer.
 */
public class PointOfSaleNotFoundException extends KernelException {

  /**
   * Constructs the exception with the point of sale identifier.
   *
   * @param id the point of sale identifier that was not found
   */
  public PointOfSaleNotFoundException(PointOfSaleId id) {
    super("Point of sale not found: " + id.value());
  }
}
