package com.entreck.event.application.usecase.impl;

import com.entreck.event.application.dto.EventSummaryResponse;
import com.entreck.event.application.usecase.FindEventsUseCase;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventSearchCriteria;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.enums.EventCategory;
import java.util.List;

/**
 * Use case for searching and listing events (B01, B02).
 *
 * <p>Supports filtering by name and/or category, with pagination. Returns
 * lightweight summaries suitable for list views.
 */
public class FindEventsUseCaseImpl implements FindEventsUseCase {

  private final EventRepository eventRepository;

  /**
   * Constructs the use case with the required port.
   *
   * @param eventRepository the event repository port
   */
  public FindEventsUseCaseImpl(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /** {@inheritDoc} */
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
}
