package com.entreck.pos.application.usecase;

import com.entreck.pos.application.dto.CreatePointOfSaleRequest;
import com.entreck.pos.application.dto.PointOfSaleResponse;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.id.ShopId;

/**
 * Port for the register-point-of-sale use case (S01).
 *
 * <p>Creates a new point of sale in ACTIVE status.
 */
public interface RegisterPointOfSaleUseCase {

  /**
   * Registers a new point of sale.
   *
   * @param request the create point of sale request
   * @param posId the identifier to assign to the new POS
   * @param shopId the shop identifier
   * @return the registered point of sale response
   */
  PointOfSaleResponse execute(CreatePointOfSaleRequest request, PointOfSaleId posId, ShopId shopId);
}
