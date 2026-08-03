package com.entreck.pos.application.usecase;

import com.entreck.pos.application.dto.PointOfSaleDetailResponse;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Port for the get-point-of-sale-detail use case (B06).
 *
 * <p>Returns a complete view of a point of sale including all fields.
 * Raises {@link PointOfSaleNotFoundException} if the POS does not exist.
 */
public interface GetPointOfSaleDetailUseCase {

  /**
   * Retrieves full detail for a point of sale.
   *
   * @param posId the identifier of the POS to retrieve
   * @return the full POS detail response
   * @throws PointOfSaleNotFoundException if the POS does not exist
   */
  PointOfSaleDetailResponse execute(PointOfSaleId posId);
}
