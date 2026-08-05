package com.entreck.shared.domain.link;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EventPointOfSaleLink")
class EventPointOfSaleLinkTest {

  private final LinkId linkId = new LinkId(1L);
  private final EventId eventId = new EventId(10L);
  private final PointOfSaleId posId = new PointOfSaleId(20L);
  private final Instant now = Instant.now();

  @Test
  @DisplayName("should create link with valid parameters")
  void shouldCreateLinkWithValidParameters() {
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.AVAILABLE, "Initial stock", now);

    assertThat(link.id()).isEqualTo(linkId);
    assertThat(link.eventId()).isEqualTo(eventId);
    assertThat(link.posId()).isEqualTo(posId);
    assertThat(link.availabilityStatus()).isEqualTo(AvailabilityStatus.AVAILABLE);
    assertThat(link.note()).isEqualTo("Initial stock");
    assertThat(link.updatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("should create link with null note")
  void shouldCreateLinkWithNullNote() {
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.UNKNOWN, null, now);

    assertThat(link.note()).isNull();
  }

  @Test
  @DisplayName("should reject null id")
  void shouldRejectNullId() {
    assertThatThrownBy(() -> new EventPointOfSaleLink(
        null, eventId, posId, AvailabilityStatus.AVAILABLE, null, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Link id is required");
  }

  @Test
  @DisplayName("should reject null event id")
  void shouldRejectNullEventId() {
    assertThatThrownBy(() -> new EventPointOfSaleLink(
        linkId, null, posId, AvailabilityStatus.AVAILABLE, null, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event id is required");
  }

  @Test
  @DisplayName("should reject null pos id")
  void shouldRejectNullPosId() {
    assertThatThrownBy(() -> new EventPointOfSaleLink(
        linkId, eventId, null, AvailabilityStatus.AVAILABLE, null, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("PointOfSale id is required");
  }

  @Test
  @DisplayName("should reject null availability status")
  void shouldRejectNullAvailabilityStatus() {
    assertThatThrownBy(() -> new EventPointOfSaleLink(
        linkId, eventId, posId, null, null, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Availability status is required");
  }

  @Test
  @DisplayName("should reject null updatedAt")
  void shouldRejectNullUpdatedAt() {
    assertThatThrownBy(() -> new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.AVAILABLE, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Updated at is required");
  }

  @Test
  @DisplayName("should change availability status")
  void shouldChangeAvailabilityStatus() {
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.AVAILABLE, "Initial", now);

    Instant newTime = now.plusSeconds(3600);
    link.changeAvailability(AvailabilityStatus.SOLD_OUT, "Sold out", newTime);

    assertThat(link.availabilityStatus()).isEqualTo(AvailabilityStatus.SOLD_OUT);
    assertThat(link.note()).isEqualTo("Sold out");
  }

  @Test
  @DisplayName("should change availability to null note")
  void shouldChangeAvailabilityToNullNote() {
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.AVAILABLE, "Initial", now);

    link.changeAvailability(AvailabilityStatus.LIMITED, null, now.plusSeconds(3600));

    assertThat(link.availabilityStatus()).isEqualTo(AvailabilityStatus.LIMITED);
    assertThat(link.note()).isNull();
  }

  @Test
  @DisplayName("should reject null status on changeAvailability")
  void shouldRejectNullStatusOnChangeAvailability() {
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.AVAILABLE, null, now);

    assertThatThrownBy(() -> link.changeAvailability(null, "note", now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Availability status is required");
  }

  @Test
  @DisplayName("should reject null instant on changeAvailability")
  void shouldRejectNullInstantOnChangeAvailability() {
    EventPointOfSaleLink link = new EventPointOfSaleLink(
        linkId, eventId, posId, AvailabilityStatus.AVAILABLE, null, now);

    assertThatThrownBy(() -> link.changeAvailability(AvailabilityStatus.LIMITED, "note", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Instant is required");
  }

  @Test
  @DisplayName("should support all availability statuses")
  void shouldSupportAllAvailabilityStatuses() {
    for (AvailabilityStatus status : AvailabilityStatus.values()) {
      EventPointOfSaleLink link = new EventPointOfSaleLink(
          linkId, eventId, posId, status, null, now);
      assertThat(link.availabilityStatus()).isEqualTo(status);
    }
  }
}
