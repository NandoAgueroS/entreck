package com.entreck.event.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link EventPointOfSaleLinkJpaEntity}.
 *
 * <p>Provides standard CRUD operations plus derived queries for link lookups.
 */
public interface SpringDataEventPointOfSaleLinkRepository
    extends JpaRepository<EventPointOfSaleLinkJpaEntity, Long> {

  /**
   * Finds all links for a given event.
   *
   * @param eventId the event identifier
   * @return a list of links for the event
   */
  List<EventPointOfSaleLinkJpaEntity> findByEventId(Long eventId);

  /**
   * Finds a specific link by event and POS identifiers.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   * @return an Optional containing the link if found, or empty otherwise
   */
  Optional<EventPointOfSaleLinkJpaEntity> findByEventIdAndPosId(Long eventId, Long posId);

  /**
   * Checks if a link already exists for the given event and POS pair.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   * @return true if a link exists, false otherwise
   */
  boolean existsByEventIdAndPosId(Long eventId, Long posId);

  /**
   * Deletes a link by event and POS identifiers.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   */
  void deleteByEventIdAndPosId(Long eventId, Long posId);
}
