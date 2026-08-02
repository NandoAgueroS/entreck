package com.entreck.event.domain.repository;

import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventSearchCriteria;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.id.EventId;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for Event aggregate persistence.
 *
 * <p>Domain-layer contract that infrastructure adapters must implement. The
 * domain layer has no dependency on Spring Data or JPA; this interface uses
 * only domain types.
 */
public interface EventRepository {

  /**
   * Saves an Event aggregate.
   *
   * @param event the event to save
   * @return the saved event (with any generated fields populated)
   */
  Event save(Event event);

  /**
   * Finds an Event by its identifier.
   *
   * @param id the event identifier
   * @return an Optional containing the event if found, or empty otherwise
   */
  Optional<Event> findById(EventId id);

  /**
   * Searches for events matching the given criteria.
   *
   * @param criteria the search criteria (name filter, category filter)
   * @param page the page number (0-based)
   * @param size the page size
   * @return a paginated result of events matching the criteria
   */
  DomainPage<Event> search(EventSearchCriteria criteria, int page, int size);

  /**
   * Finds events by exact name match.
   *
   * @param name the event name
   * @return a list of events with the exact name
   */
  List<Event> findByName(String name);

  /**
   * Checks if an event with the given name already exists.
   *
   * @param name the event name
   * @return true if an event with that name exists, false otherwise
   */
  boolean existsByName(String name);
}
