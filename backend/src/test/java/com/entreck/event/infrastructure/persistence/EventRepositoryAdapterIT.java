package com.entreck.event.infrastructure.persistence;

import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventSearchCriteria;
import com.entreck.event.domain.EventStatus;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import com.entreck.shared.testing.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link EventRepositoryAdapter} using Testcontainers.
 *
 * <p>Verifies the full persistence flow: domain → JPA entity → PostgreSQL →
 * JPA entity → domain. Uses a real PostgreSQL 16 instance via Testcontainers
 * to validate JPA mappings, constraints, and pagination behavior.
 */
@DataJpaTest
@Import(EventRepositoryAdapter.class)
class EventRepositoryAdapterIT extends BaseIntegrationTest {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private SpringDataEventRepository springDataRepo;

  @Test
  void saveAndFindById_roundTrip() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Event event = new Event(
        new EventId(0L), "Rock Concert", EventCategory.CONCERT,
        now.plusSeconds(86400), "A great rock concert",
        new OrganizerId(1L), EventStatus.PUBLISHED, now, now);

    Event saved = eventRepository.save(event);

    assertThat(saved.id().value()).isNotNull().isPositive();
    assertThat(saved.name()).isEqualTo("Rock Concert");
    assertThat(saved.category()).isEqualTo(EventCategory.CONCERT);
    assertThat(saved.status()).isEqualTo(EventStatus.PUBLISHED);
    assertThat(saved.organizerId().value()).isEqualTo(1L);

    Optional<Event> found = eventRepository.findById(saved.id());
    assertThat(found).isPresent();
    assertThat(found.get().name()).isEqualTo("Rock Concert");
  }

  @Test
  void save_existingEvent_updatesFields() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Event event = new Event(
        new EventId(0L), "Original Name", EventCategory.THEATER,
        now.plusSeconds(86400), "Description",
        new OrganizerId(1L), EventStatus.DRAFT, now, now);
    Event saved = eventRepository.save(event);

    saved.update("Updated Name", EventCategory.SPORTS, now.plusSeconds(172800), "New desc");
    Event updated = eventRepository.save(saved);

    assertThat(updated.name()).isEqualTo("Updated Name");
    assertThat(updated.category()).isEqualTo(EventCategory.SPORTS);
    assertThat(updated.id()).isEqualTo(saved.id());
  }

  @Test
  void search_returnsAllWhenNoFilters() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    saveTestEvent("Event A", EventCategory.CONCERT, now);
    saveTestEvent("Event B", EventCategory.SPORTS, now);
    saveTestEvent("Event C", EventCategory.THEATER, now);

    DomainPage<Event> result = eventRepository.search(
        EventSearchCriteria.all(), 0, 10);

    assertThat(result.content()).hasSize(3);
    assertThat(result.meta().totalElements()).isEqualTo(3);
  }

  @Test
  void search_filtersByName() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    saveTestEvent("Music Fest", EventCategory.FESTIVAL, now);
    saveTestEvent("Sports Day", EventCategory.SPORTS, now);

    DomainPage<Event> result = eventRepository.search(
        EventSearchCriteria.byName("Music Fest"), 0, 10);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().get(0).name()).isEqualTo("Music Fest");
  }

  @Test
  void search_filtersByCategory() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    saveTestEvent("Concert 1", EventCategory.CONCERT, now);
    saveTestEvent("Concert 2", EventCategory.CONCERT, now);
    saveTestEvent("Game 1", EventCategory.SPORTS, now);

    DomainPage<Event> result = eventRepository.search(
        EventSearchCriteria.byCategory(EventCategory.CONCERT), 0, 10);

    assertThat(result.content()).hasSize(2);
  }

  @Test
  void search_filtersByNameAndCategory() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    saveTestEvent("Rock Fest", EventCategory.FESTIVAL, now);
    saveTestEvent("Jazz Fest", EventCategory.FESTIVAL, now);
    saveTestEvent("Rock Concert", EventCategory.CONCERT, now);

    DomainPage<Event> result = eventRepository.search(
        new EventSearchCriteria("Rock Fest", EventCategory.FESTIVAL), 0, 10);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().get(0).name()).isEqualTo("Rock Fest");
  }

  @Test
  void search_paginationReturnsCorrectMetadata() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    for (int i = 1; i <= 5; i++) {
      saveTestEvent("Event " + i, EventCategory.OTHER, now);
    }

    DomainPage<Event> page0 = eventRepository.search(EventSearchCriteria.all(), 0, 2);
    assertThat(page0.content()).hasSize(2);
    assertThat(page0.meta().page()).isZero();
    assertThat(page0.meta().totalElements()).isEqualTo(5);
    assertThat(page0.meta().totalPages()).isEqualTo(3);

    DomainPage<Event> page2 = eventRepository.search(EventSearchCriteria.all(), 2, 2);
    assertThat(page2.content()).hasSize(1);
    assertThat(page2.meta().page()).isEqualTo(2);
  }

  @Test
  void findByName_returnsExactMatches() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    saveTestEvent("Unique Event", EventCategory.CONCERT, now);
    saveTestEvent("Other Event", EventCategory.SPORTS, now);

    List<Event> found = eventRepository.findByName("Unique Event");
    assertThat(found).hasSize(1);
    assertThat(found.get(0).name()).isEqualTo("Unique Event");
  }

  @Test
  void existsByName_returnsTrueForExisting() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    saveTestEvent("Existing Event", EventCategory.OTHER, now);

    assertThat(eventRepository.existsByName("Existing Event")).isTrue();
    assertThat(eventRepository.existsByName("Nonexistent")).isFalse();
  }

  @Test
  void uniqueNameConstraint_rejectsDuplicate() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    saveTestEvent("Duplicate Name", EventCategory.CONCERT, now);

    assertThatThrownBy(() ->
        saveTestEvent("Duplicate Name", EventCategory.SPORTS, now))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   * Helper to save a test event with auto-generated ID via adapter.
   *
   * @param name the event name
   * @param category the event category
   * @param eventDate the event date
   * @return the saved event with real ID
   */
  private Event saveTestEvent(String name, EventCategory category, Instant eventDate) {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Event event = new Event(
        new EventId(0L), name, category, eventDate, "Test description",
        new OrganizerId(1L), EventStatus.DRAFT, now, now);
    return eventRepository.save(event);
  }
}
