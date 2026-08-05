package com.entreck.event.application.usecase.impl;

import com.entreck.event.application.dto.AssociateRequest;
import com.entreck.event.application.dto.LinkedPosResponse;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.exception.LinkAlreadyExistsException;
import com.entreck.event.application.usecase.AssociatePosToEventUseCase;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.link.LinkId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;
import java.time.Instant;

/**
 * Use case for associating a PointOfSale with an Event (O03).
 *
 * <p>Creates a new EventPointOfSaleLink with default UNKNOWN availability.
 * Validates that both the Event and POS exist, and that no duplicate link
 * already exists.
 */
public class AssociatePosToEventUseCaseImpl implements AssociatePosToEventUseCase {

  private final EventRepository eventRepository;
  private final PointOfSaleRepository pointOfSaleRepository;
  private final EventPointOfSaleLinkRepository linkRepository;

  /**
   * Constructs the use case with the required ports.
   *
   * @param eventRepository the event repository port
   * @param pointOfSaleRepository the POS repository port
   * @param linkRepository the link repository port
   */
  public AssociatePosToEventUseCaseImpl(
      EventRepository eventRepository,
      PointOfSaleRepository pointOfSaleRepository,
      EventPointOfSaleLinkRepository linkRepository) {
    this.eventRepository = eventRepository;
    this.pointOfSaleRepository = pointOfSaleRepository;
    this.linkRepository = linkRepository;
  }

  /** {@inheritDoc} */
  @Override
  public LinkedPosResponse execute(EventId eventId, AssociateRequest request) {
    if (eventRepository.findById(eventId).isEmpty()) {
      throw new EventNotFoundException(eventId);
    }

    PointOfSaleId posId = new PointOfSaleId(request.posId());
    if (pointOfSaleRepository.findById(posId).isEmpty()) {
      throw new com.entreck.pos.application.exception.PointOfSaleNotFoundException(posId);
    }

    if (linkRepository.existsByEventIdAndPosId(eventId, posId)) {
      throw new LinkAlreadyExistsException(eventId, posId);
    }

    Instant now = Instant.now();
    LinkId linkId = new LinkId(0L); // placeholder; adapter lets JPA generate
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.UNKNOWN, request.note(), now);
    EventPointOfSaleLink saved = linkRepository.save(link);

    return new LinkedPosResponse(
        saved.id().value(),
        saved.eventId().value(),
        saved.posId().value(),
        null, // POS name resolved at adapter level if needed
        saved.availabilityStatus(),
        saved.note(),
        saved.updatedAt());
  }
}
