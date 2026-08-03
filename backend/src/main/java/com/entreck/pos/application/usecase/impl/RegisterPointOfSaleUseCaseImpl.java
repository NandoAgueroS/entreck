package com.entreck.pos.application.usecase.impl;

import com.entreck.pos.application.dto.CreatePointOfSaleRequest;
import com.entreck.pos.application.dto.PointOfSaleResponse;
import com.entreck.pos.application.mapper.PointOfSaleMapper;
import com.entreck.pos.application.usecase.RegisterPointOfSaleUseCase;
import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.valueobject.Address;
import com.entreck.pos.domain.valueobject.Contact;
import com.entreck.pos.domain.valueobject.GeoLocation;
import com.entreck.pos.domain.valueobject.OpeningHours;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.id.ShopId;
import java.time.Instant;

/**
 * Use case for registering a new point of sale (S01).
 *
 * <p>Creates a PointOfSale in ACTIVE status and saves it.
 */
public class RegisterPointOfSaleUseCaseImpl implements RegisterPointOfSaleUseCase {

  private final PointOfSaleRepository pointOfSaleRepository;
  private final PointOfSaleMapper pointOfSaleMapper;

  /**
   * Constructs the use case with the required port.
   *
   * @param pointOfSaleRepository the POS repository port
   * @param pointOfSaleMapper the POS mapper
   */
  public RegisterPointOfSaleUseCaseImpl(
      PointOfSaleRepository pointOfSaleRepository,
      PointOfSaleMapper pointOfSaleMapper) {
    this.pointOfSaleRepository = pointOfSaleRepository;
    this.pointOfSaleMapper = pointOfSaleMapper;
  }

  /**
   * Executes the use case.
   *
   * @param request the create point of sale request
   * @param posId the identifier to assign to the new POS
   * @param shopId the shop identifier
   * @return the registered point of sale response
   */
  @Override
  public PointOfSaleResponse execute(
      CreatePointOfSaleRequest request, PointOfSaleId posId, ShopId shopId) {
    Address address = pointOfSaleMapper.toAddress(request.address());
    GeoLocation geoLocation = pointOfSaleMapper.toGeoLocation(request.location());
    Contact contact = pointOfSaleMapper.toContact(request.contact());
    OpeningHours openingHours = pointOfSaleMapper.toOpeningHours(request.openingHours());

    Instant now = Instant.now();
    PointOfSale pos = new PointOfSale(
        posId,
        request.name(),
        address,
        geoLocation,
        contact,
        openingHours,
        shopId,
        now,
        now);

    PointOfSale saved = pointOfSaleRepository.save(pos);
    return pointOfSaleMapper.toResponse(saved);
  }
}
