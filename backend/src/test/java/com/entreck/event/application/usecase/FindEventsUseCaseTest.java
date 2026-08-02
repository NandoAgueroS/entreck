package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventSummaryResponse;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventSearchCriteria;
import com.entreck.event.domain.EventStatus;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.PageMeta;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindEventsUseCase")
class FindEventsUseCaseTest {

  @Mock
  private EventRepository eventRepository;

  private FindEventsUseCase useCase;

  private final EventId eventId1 = new EventId(1L);
  private final EventId eventId2 = new EventId(2L);
  private final OrganizerId organizerId = new OrganizerId(10L);
  private final Instant now = Instant.now();

  private Event createEvent(EventId id, String name, EventCategory category) {
    return new Event(
        id,
        name,
        category,
        now.plusSeconds(86400),
        "Description for " + name,
        organizerId,
        EventStatus.PUBLISHED,
        now,
        now);
  }

  @BeforeEach
  void setUp() {
    useCase = new FindEventsUseCase(eventRepository);
  }

  @Test
  @DisplayName("should search events with no filters")
  void shouldSearchEventsWithNoFilters() {
    Event event1 = createEvent(eventId1, "Concert 1", EventCategory.CONCERT);
    Event event2 = createEvent(eventId2, "Concert 2", EventCategory.CONCERT);
    PageMeta meta = PageMeta.of(0, 20, 2, 1);
    DomainPage<Event> domainPage = DomainPage.of(List.of(event1, event2), meta);

    when(eventRepository.search(any(EventSearchCriteria.class), eq(0), eq(20)))
        .thenReturn(domainPage);

    DomainPage<EventSummaryResponse> result = useCase.execute(null, null, 0, 20);

    assertThat(result.content()).hasSize(2);
    assertThat(result.content().get(0).name()).isEqualTo("Concert 1");
    assertThat(result.content().get(1).name()).isEqualTo("Concert 2");
    assertThat(result.meta().totalElements()).isEqualTo(2);
  }

  @Test
  @DisplayName("should search events with name filter")
  void shouldSearchEventsWithNameFilter() {
    Event event = createEvent(eventId1, "Jazz Night", EventCategory.CONCERT);
    PageMeta meta = PageMeta.of(0, 20, 1, 1);
    DomainPage<Event> domainPage = DomainPage.of(List.of(event), meta);

    ArgumentCaptor<EventSearchCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(EventSearchCriteria.class);

    when(eventRepository.search(criteriaCaptor.capture(), eq(0), eq(20)))
        .thenReturn(domainPage);

    useCase.execute("Jazz Night", null, 0, 20);

    assertThat(criteriaCaptor.getValue().name()).isEqualTo("Jazz Night");
    assertThat(criteriaCaptor.getValue().category()).isNull();
  }

  @Test
  @DisplayName("should search events with category filter")
  void shouldSearchEventsWithCategoryFilter() {
    Event event = createEvent(eventId1, "Sports Event", EventCategory.SPORTS);
    PageMeta meta = PageMeta.of(0, 20, 1, 1);
    DomainPage<Event> domainPage = DomainPage.of(List.of(event), meta);

    ArgumentCaptor<EventSearchCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(EventSearchCriteria.class);

    when(eventRepository.search(criteriaCaptor.capture(), eq(0), eq(20)))
        .thenReturn(domainPage);

    useCase.execute(null, EventCategory.SPORTS, 0, 20);

    assertThat(criteriaCaptor.getValue().name()).isNull();
    assertThat(criteriaCaptor.getValue().category()).isEqualTo(EventCategory.SPORTS);
  }

  @Test
  @DisplayName("should return empty list when no events match")
  void shouldReturnEmptyListWhenNoEventsMatch() {
    PageMeta meta = PageMeta.of(0, 20, 0, 0);
    DomainPage<Event> domainPage = DomainPage.of(List.of(), meta);

    when(eventRepository.search(any(EventSearchCriteria.class), eq(0), eq(20)))
        .thenReturn(domainPage);

    DomainPage<EventSummaryResponse> result = useCase.execute(null, null, 0, 20);

    assertThat(result.content()).isEmpty();
    assertThat(result.meta().totalElements()).isZero();
  }
}
