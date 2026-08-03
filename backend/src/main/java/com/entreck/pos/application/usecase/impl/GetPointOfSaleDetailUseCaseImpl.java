package com.entreck.pos.application.usecase.impl;

import com.entreck.pos.application.dto.PointOfSaleDetailResponse;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.pos.application.mapper.PointOfSaleMapper;
import com.entreck.pos.application.usecase.GetPointOfSaleDetailUseCase;
import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Use case for retrieving full POS detail (B06).
 *
 * <p>Returns a complete view of the POS including all fields. Raises
 * {@link PointOfSaleNotFoundException} if the POS does not exist.
 */
public class GetPointOfSaleDetailUseCaseImpl implements GetPointOfSaleDetailUseCase {

  private final PointOfSaleRepository pointOfSaleRepository;
  private final PointOfSaleMapper pointOfSaleMapper;

  /**
   * Constructs the use case with the required port.
   *
   * @param pointOfSaleRepository the POS repository port
   * @param pointOfSaleMapper the POS mapper
   */
  public GetPointOfSaleDetailUseCaseImpl(
      PointOfSaleRepository pointOfSaleRepository,
      PointOfSaleMapper pointOfSaleMapper) {
    this.pointOfSaleRepository = pointOfSaleRepository;
    this.pointOfSaleMapper = pointOfSaleMapper;
  }

  /**
   * Executes the use case.
   *
   * @param posId the identifier of the POS to retrieve
   * @return the full POS detail response
   * @throws PointOfSaleNotFoundException if the POS does not exist
   */
  @Override
  public PointOfSaleDetailResponse execute(PointOfSaleId posId) {
    PointOfSale pos = pointOfSaleRepository.findById(posId)
        .orElseThrow(() -> new PointOfSaleNotFoundException(posId));
    return pointOfSaleMapper.toDetailResponse(pos);
  }
}
