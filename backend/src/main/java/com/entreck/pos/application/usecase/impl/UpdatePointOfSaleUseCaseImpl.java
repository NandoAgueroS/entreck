package com.entreck.pos.application.usecase.impl;

import com.entreck.pos.application.dto.PointOfSaleResponse;
import com.entreck.pos.application.dto.UpdatePointOfSaleRequest;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.pos.application.mapper.PointOfSaleMapper;
import com.entreck.pos.application.usecase.UpdatePointOfSaleUseCase;
import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.valueobject.Address;
import com.entreck.pos.domain.valueobject.Contact;
import com.entreck.pos.domain.valueobject.GeoLocation;
import com.entreck.pos.domain.valueobject.OpeningHours;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Use case for updating an existing point of sale (S02).
 *
 * <p>Applies partial updates (PATCH semantics) to a POS. Only non-null
 * fields in the request are applied.
 */
public class UpdatePointOfSaleUseCaseImpl implements UpdatePointOfSaleUseCase {

  private final PointOfSaleRepository pointOfSaleRepository;
  private final PointOfSaleMapper pointOfSaleMapper;

  /**
   * Constructs the use case with the required port.
   *
   * @param pointOfSaleRepository the POS repository port
   * @param pointOfSaleMapper the POS mapper
   */
  public UpdatePointOfSaleUseCaseImpl(
      PointOfSaleRepository pointOfSaleRepository,
      PointOfSaleMapper pointOfSaleMapper) {
    this.pointOfSaleRepository = pointOfSaleRepository;
    this.pointOfSaleMapper = pointOfSaleMapper;
  }

  /**
   * Executes the use case.
   *
   * @param posId the identifier of the POS to update
   * @param request the update request with partial fields
   * @return the updated point of sale response
   * @throws PointOfSaleNotFoundException if the POS does not exist
   */
  @Override
  public PointOfSaleResponse execute(PointOfSaleId posId, UpdatePointOfSaleRequest request) {
    PointOfSale pos = pointOfSaleRepository.findById(posId)
        .orElseThrow(() -> new PointOfSaleNotFoundException(posId));

    Address address = pointOfSaleMapper.toAddress(request.address());
    GeoLocation geoLocation = pointOfSaleMapper.toGeoLocation(request.location());
    Contact contact = pointOfSaleMapper.toContact(request.contact());
    OpeningHours openingHours = pointOfSaleMapper.toOpeningHours(request.openingHours());

    pos.update(request.name(), address, geoLocation, contact, openingHours);

    PointOfSale saved = pointOfSaleRepository.save(pos);
    return pointOfSaleMapper.toResponse(saved);
  }
}
