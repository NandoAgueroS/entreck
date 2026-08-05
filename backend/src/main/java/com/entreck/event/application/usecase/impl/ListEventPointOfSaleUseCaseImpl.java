package com.entreck.event.application.usecase.impl;

import com.entreck.event.application.dto.LinkedPosResponse;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.usecase.ListEventPointOfSaleUseCase;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;
import java.util.List;

/**
 * Use case for listing all POS records linked to an Event (B04).
 *
 * <p>Returns the linked POS records with their availability status and metadata.
 */
public class ListEventPointOfSaleUseCaseImpl implements ListEventPointOfSaleUseCase {

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
  public ListEventPointOfSaleUseCaseImpl(
      EventRepository eventRepository,
      PointOfSaleRepository pointOfSaleRepository,
      EventPointOfSaleLinkRepository linkRepository) {
    this.eventRepository = eventRepository;
    this.pointOfSaleRepository = pointOfSaleRepository;
    this.linkRepository = linkRepository;
  }

  /** {@inheritDoc} */
  @Override
  public List<LinkedPosResponse> execute(EventId eventId) {
    if (eventRepository.findById(eventId).isEmpty()) {
      throw new EventNotFoundException(eventId);
    }

    List<EventPointOfSaleLink> links = linkRepository.findByEventId(eventId);
    return links.stream()
        .map(link -> {
          String posName = pointOfSaleRepository.findById(link.posId())
              .map(pos -> pos.name())
              .orElse(null);
          return new LinkedPosResponse(
              link.id().value(),
              link.eventId().value(),
              link.posId().value(),
              posName,
              link.availabilityStatus(),
              link.note(),
              link.updatedAt());
        })
        .toList();
  }
}
