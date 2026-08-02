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
   * Searches events with optional filters.
   *
   * @param name optional name filter (exact match), or null for all
   * @param category optional category filter, or null for all
   * @param page the page number (0-based)
   * @param size the page size
   * @return a paginated result of event summaries
   */
  DomainPage<EventSummaryResponse> execute(String name, EventCategory category, int page, int size);
}
