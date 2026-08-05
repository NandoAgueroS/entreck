package com.entreck.event.application.usecase.impl;

import com.entreck.event.application.dto.AvailabilityRequest;
import com.entreck.event.application.dto.AvailabilityResponse;
import com.entreck.event.application.exception.LinkNotFoundException;
import com.entreck.event.application.usecase.UpdateAvailabilityUseCase;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;
import java.time.Instant;

/**
 * Use case for updating the availability of an EventPointOfSaleLink (S03).
 *
 * <p>Replaces the availability status and optional note for an existing link.
 * The link must exist; otherwise a {@link LinkNotFoundException} is thrown.
 */
public class UpdateAvailabilityUseCaseImpl implements UpdateAvailabilityUseCase {

  private final EventPointOfSaleLinkRepository linkRepository;

  /**
   * Constructs the use case with the required port.
   *
   * @param linkRepository the link repository port
   */
  public UpdateAvailabilityUseCaseImpl(EventPointOfSaleLinkRepository linkRepository) {
    this.linkRepository = linkRepository;
  }

  /** {@inheritDoc} */
  @Override
  public AvailabilityResponse execute(
      EventId eventId, PointOfSaleId posId, AvailabilityRequest request) {
    EventPointOfSaleLink link = linkRepository.findByEventIdAndPosId(eventId, posId)
        .orElseThrow(() -> new LinkNotFoundException(eventId, posId));

    Instant now = Instant.now();
    link.changeAvailability(request.availabilityStatus(), request.note(), now);
    EventPointOfSaleLink saved = linkRepository.save(link);

    return new AvailabilityResponse(
        saved.id().value(),
        saved.eventId().value(),
        saved.posId().value(),
        saved.availabilityStatus(),
        saved.note(),
        saved.updatedAt());
  }
}
