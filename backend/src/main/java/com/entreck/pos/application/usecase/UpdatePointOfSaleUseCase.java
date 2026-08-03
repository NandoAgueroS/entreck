package com.entreck.pos.application.usecase;

import com.entreck.pos.application.dto.PointOfSaleResponse;
import com.entreck.pos.application.dto.UpdatePointOfSaleRequest;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Port for the update-point-of-sale use case (S02).
 *
 * <p>Applies partial updates (PATCH semantics) to an existing point of sale.
 * Only non-null fields in the request are applied.
 */
public interface UpdatePointOfSaleUseCase {

  /**
   * Updates an existing point of sale.
   *
   * @param posId the identifier of the POS to update
   * @param request the update request with partial fields
   * @return the updated point of sale response
   * @throws PointOfSaleNotFoundException if the POS does not exist
   */
  PointOfSaleResponse execute(PointOfSaleId posId, UpdatePointOfSaleRequest request);
}
