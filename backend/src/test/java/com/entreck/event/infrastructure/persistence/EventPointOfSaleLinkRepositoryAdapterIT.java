package com.entreck.event.infrastructure.persistence;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.link.LinkId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;
import com.entreck.shared.testing.BaseIntegrationTest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link EventPointOfSaleLinkRepositoryAdapter}.
 *
 * <p>Uses {@link DataJpaTest} with Testcontainers PostgreSQL to verify
 * the unique constraint, round-trip mapping, and deletion behavior.
 */
@DataJpaTest
@DisplayName("EventPointOfSaleLinkRepositoryAdapter")
class EventPointOfSaleLinkRepositoryAdapterIT extends BaseIntegrationTest {

  @Autowired
  private SpringDataEventPointOfSaleLinkRepository springDataRepo;

  private EventPointOfSaleLinkRepositoryAdapter adapter;

  private final EventId eventId = new EventId(100L);
  private final PointOfSaleId posId = new PointOfSaleId(200L);

  @BeforeEach
  void setUp() {
    adapter = new EventPointOfSaleLinkRepositoryAdapter(springDataRepo);
    springDataRepo.deleteAll();
  }

  @Test
  @DisplayName("should save and retrieve a link")
  void shouldSaveAndRetrieveLink() {
    Instant now = Instant.now();
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId, AvailabilityStatus.AVAILABLE, "First stock", now);

    EventPointOfSaleLink saved = adapter.save(link);

    assertThat(saved.id().value()).isNotNull();
    assertThat(saved.eventId()).isEqualTo(eventId);
    assertThat(saved.posId()).isEqualTo(posId);
    assertThat(saved.availabilityStatus()).isEqualTo(AvailabilityStatus.AVAILABLE);
    assertThat(saved.note()).isEqualTo("First stock");

    Optional<EventPointOfSaleLink> found = adapter.findByEventIdAndPosId(eventId, posId);
    assertThat(found).isPresent();
    assertThat(found.get().availabilityStatus()).isEqualTo(AvailabilityStatus.AVAILABLE);
  }

  @Test
  @DisplayName("should enforce unique constraint on (event_id, pos_id)")
  void shouldEnforceUniqueConstraint() {
    Instant now = Instant.now();
    EventPointOfSaleLink link1 = new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId, AvailabilityStatus.AVAILABLE, null, now);
    adapter.save(link1);

    EventPointOfSaleLink link2 = new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId, AvailabilityStatus.LIMITED, null, now);

    assertThatThrownBy(() -> adapter.save(link2))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("should update availability status via save")
  void shouldUpdateAvailabilityStatus() {
    Instant now = Instant.now();
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId, AvailabilityStatus.UNKNOWN, null, now);
    EventPointOfSaleLink saved = adapter.save(link);

    saved.changeAvailability(AvailabilityStatus.SOLD_OUT, "Sold out", now.plusSeconds(3600));
    adapter.save(saved);

    Optional<EventPointOfSaleLink> found = adapter.findByEventIdAndPosId(eventId, posId);
    assertThat(found).isPresent();
    assertThat(found.get().availabilityStatus()).isEqualTo(AvailabilityStatus.SOLD_OUT);
    assertThat(found.get().note()).isEqualTo("Sold out");
  }

  @Test
  @DisplayName("should delete link by event and POS identifiers")
  void shouldDeleteLinkByEventAndPos() {
    Instant now = Instant.now();
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId, AvailabilityStatus.AVAILABLE, null, now);
    adapter.save(link);

    adapter.delete(eventId, posId);

    Optional<EventPointOfSaleLink> found = adapter.findByEventIdAndPosId(eventId, posId);
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("should find all links for an event")
  void shouldFindAllLinksForEvent() {
    Instant now = Instant.now();
    PointOfSaleId posId2 = new PointOfSaleId(300L);

    adapter.save(new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId, AvailabilityStatus.AVAILABLE, null, now));
    adapter.save(new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId2, AvailabilityStatus.LIMITED, null, now));

    List<EventPointOfSaleLink> links = adapter.findByEventId(eventId);
    assertThat(links).hasSize(2);
  }

  @Test
  @DisplayName("should return false for existsByEventIdAndPosId when no link")
  void shouldReturnFalseWhenNoLink() {
    assertThat(adapter.existsByEventIdAndPosId(eventId, posId)).isFalse();
  }

  @Test
  @DisplayName("should return true for existsByEventIdAndPosId when link exists")
  void shouldReturnTrueWhenLinkExists() {
    Instant now = Instant.now();
    adapter.save(new EventPointOfSaleLink(
        new LinkId(0L), eventId, posId, AvailabilityStatus.AVAILABLE, null, now));

    assertThat(adapter.existsByEventIdAndPosId(eventId, posId)).isTrue();
  }
}
