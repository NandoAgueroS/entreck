package com.entreck.event.domain;

import com.entreck.shared.domain.enums.EventCategory;

/**
 * Criteria for searching events.
 *
 * <p>Immutable value object carrying optional filters for name and category.
 * Null fields indicate "no filter" (match all).
 *
 * @param name optional name filter (exact match)
 * @param category optional category filter
 */
public record EventSearchCriteria(String name, EventCategory category) {

  /**
   * Creates search criteria with no filters (match all events).
   *
   * @return empty search criteria
   */
  public static EventSearchCriteria all() {
    return new EventSearchCriteria(null, null);
  }

  /**
   * Creates search criteria filtering by name only.
   *
   * @param name the name filter
   * @return search criteria with name filter
   */
  public static EventSearchCriteria byName(String name) {
    return new EventSearchCriteria(name, null);
  }

  /**
   * Creates search criteria filtering by category only.
   *
   * @param category the category filter
   * @return search criteria with category filter
   */
  public static EventSearchCriteria byCategory(EventCategory category) {
    return new EventSearchCriteria(null, category);
  }
}
