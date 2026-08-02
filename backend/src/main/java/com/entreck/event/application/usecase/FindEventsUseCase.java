package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventSummaryResponse;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventSearchCriteria;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import java.util.List;

/**
 * Use case for searching and listing events (B01, B02).
 *
 * <p>Supports filtering by name and/or category, with pagination. Returns
 * lightweight summaries suitable for list views.
 */
public class FindEventsUseCase implements FindEventsUseCaseInterface {

  private final EventRepository eventRepository;

  /**
   * Constructs the use case with the required port.
   *
   * @param eventRepository the event repository port
   */
  public FindEventsUseCase(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /**
   * Executes the use case with optional filters.
   *
   * @param name optional name filter (exact match), or null for all
   * @param category optional category filter, or null for all
   * @param page the page number (0-based)
   * @param size the page size
   * @return a paginated result of event summaries
   */
  @Override
  public DomainPage<EventSummaryResponse> execute(
      String name, EventCategory category, int page, int size) {
    EventSearchCriteria criteria = new EventSearchCriteria(name, category);
    DomainPage<Event> domainPage = eventRepository.search(criteria, page, size);

    List<EventSummaryResponse> summaries = domainPage.content().stream()
        .map(EventSummaryResponse::from)
        .toList();

    return DomainPage.of(summaries, domainPage.meta());
  }

  /**
   * Finds events by exact name match.
   *
   * @param name the event name
   * @return a list of event summaries with the exact name
   */
  public List<EventSummaryResponse> findByName(String name) {
    return eventRepository.findByName(name).stream()
        .map(EventSummaryResponse::from)
        .toList();
  }
}
