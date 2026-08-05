package com.entreck.event.application.usecase.impl;

import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.exception.LinkNotFoundException;
import com.entreck.event.application.usecase.DissociatePosFromEventUseCase;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;

/**
 * Use case for dissociating a PointOfSale from an Event (O04).
 *
 * <p>Removes an existing EventPointOfSaleLink. Validates that both the Event
 * and POS exist, and that a link exists to be removed.
 */
public class DissociatePosFromEventUseCaseImpl implements DissociatePosFromEventUseCase {

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
  public DissociatePosFromEventUseCaseImpl(
      EventRepository eventRepository,
      PointOfSaleRepository pointOfSaleRepository,
      EventPointOfSaleLinkRepository linkRepository) {
    this.eventRepository = eventRepository;
    this.pointOfSaleRepository = pointOfSaleRepository;
    this.linkRepository = linkRepository;
  }

  /** {@inheritDoc} */
  @Override
  public void execute(EventId eventId, PointOfSaleId posId) {
    if (eventRepository.findById(eventId).isEmpty()) {
      throw new EventNotFoundException(eventId);
    }

    if (pointOfSaleRepository.findById(posId).isEmpty()) {
      throw new com.entreck.pos.application.exception.PointOfSaleNotFoundException(posId);
    }

    if (!linkRepository.existsByEventIdAndPosId(eventId, posId)) {
      throw new LinkNotFoundException(eventId, posId);
    }

    linkRepository.delete(eventId, posId);
  }
}
