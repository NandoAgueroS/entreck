package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventSummaryResponse;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.enums.EventCategory;

/**
 * Port for the find-events use case (B01, B02).
 *
 * <p>Supports searching and listing events with optional filters by name
 * and/or category, with pagination. Returns lightweight summaries suitable
 * for list views.
 */
public interface FindEventsUseCaseInterface {

  /**
   * Searches for events by optional name and/or category filters, with pagination.
   *
   * @param name optional exact-match name filter, or null for all events
   * @param category optional category filter, or null for all categories
   * @param page the 0-based page number
   * @param size the page size
   * @return a paginated result of event summaries
   * @throws IllegalArgumentException if page is negative or size is not positive
   */
  DomainPage<EventSummaryResponse> execute(String name, EventCategory category, int page, int size);
}
