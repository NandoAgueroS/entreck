package com.entreck.event.infrastructure.persistence;

import com.entreck.shared.domain.enums.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for {@link EventJpaEntity}.
 *
 * <p>Provides derived queries for name and category filtering with pagination.
 * This is an infrastructure concern; the domain port {@code EventRepository} is
 * implemented by {@link EventRepositoryAdapter}.
 */
public interface SpringDataEventRepository extends JpaRepository<EventJpaEntity, Long> {

  /**
   * Checks whether an event with the given name exists.
   *
   * @param name the event name
   * @return true if an event with that name exists
   */
  boolean existsByName(String name);

  /**
   * Finds all events with the exact given name.
   *
   * @param name the event name
   * @return the list of matching entities
   */
  List<EventJpaEntity> findByName(String name);

  /**
   * Finds events filtered by name with pagination.
   *
   * @param name the name filter
   * @param pageable the pagination request
   * @return a page of matching entities
   */
  Page<EventJpaEntity> findByName(String name, Pageable pageable);

  /**
   * Finds events filtered by category with pagination.
   *
   * @param category the category filter
   * @param pageable the pagination request
   * @return a page of matching entities
   */
  Page<EventJpaEntity> findByCategory(EventCategory category, Pageable pageable);

  /**
   * Finds events filtered by both name and category with pagination.
   *
   * @param name the name filter
   * @param category the category filter
   * @param pageable the pagination request
   * @return a page of matching entities
   */
  Page<EventJpaEntity> findByNameAndCategory(String name, EventCategory category, Pageable pageable);
}
