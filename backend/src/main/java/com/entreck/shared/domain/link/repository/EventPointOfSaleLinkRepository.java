package com.entreck.shared.domain.link.repository;

import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for EventPointOfSaleLink aggregate persistence.
 *
 * <p>Domain-layer contract that infrastructure adapters must implement. The
 * domain layer has no dependency on Spring Data or JPA; this interface uses
 * only domain types.
 */
public interface EventPointOfSaleLinkRepository {

  /**
   * Saves an EventPointOfSaleLink aggregate.
   *
   * @param link the link to save
   * @return the saved link (with any generated fields populated)
   */
  EventPointOfSaleLink save(EventPointOfSaleLink link);

  /**
   * Finds all links for a given event.
   *
   * @param eventId the event identifier
   * @return a list of links for the event
   */
  List<EventPointOfSaleLink> findByEventId(EventId eventId);

  /**
   * Finds a specific link by event and POS identifiers.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   * @return an Optional containing the link if found, or empty otherwise
   */
  Optional<EventPointOfSaleLink> findByEventIdAndPosId(EventId eventId, PointOfSaleId posId);

  /**
   * Checks if a link already exists for the given event and POS pair.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   * @return true if a link exists, false otherwise
   */
  boolean existsByEventIdAndPosId(EventId eventId, PointOfSaleId posId);

  /**
   * Deletes a link by event and POS identifiers.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   */
  void delete(EventId eventId, PointOfSaleId posId);
}
