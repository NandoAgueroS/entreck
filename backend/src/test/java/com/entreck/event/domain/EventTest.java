package com.entreck.event.domain;

import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Event aggregate")
class EventTest {

  private final EventId eventId = new EventId(1L);
  private final OrganizerId organizerId = new OrganizerId(10L);
  private final Instant now = Instant.now();

  private Event createDraftEvent() {
    return new Event(
        eventId,
        "Concert Night",
        EventCategory.CONCERT,
        now.plusSeconds(86400),
        "A great concert",
        organizerId,
        EventStatus.DRAFT,
        now,
        now);
  }

  @Test
  @DisplayName("should create event in DRAFT status")
  void shouldCreateEventInDraftStatus() {
    Event event = createDraftEvent();

    assertThat(event.id()).isEqualTo(eventId);
    assertThat(event.name()).isEqualTo("Concert Night");
    assertThat(event.category()).isEqualTo(EventCategory.CONCERT);
    assertThat(event.status()).isEqualTo(EventStatus.DRAFT);
    assertThat(event.organizerId()).isEqualTo(organizerId);
  }

  @Test
  @DisplayName("should transition from DRAFT to PUBLISHED")
  void shouldTransitionFromDraftToPublished() {
    Event event = createDraftEvent();

    event.publish();

    assertThat(event.status()).isEqualTo(EventStatus.PUBLISHED);
  }

  @Test
  @DisplayName("should transition from PUBLISHED to CANCELLED")
  void shouldTransitionFromPublishedToCancelled() {
    Event event = createDraftEvent();
    event.publish();

    event.cancel();

    assertThat(event.status()).isEqualTo(EventStatus.CANCELLED);
  }

  @Test
  @DisplayName("should NOT allow publishing a non-DRAFT event")
  void shouldNotAllowPublishingNonDraftEvent() {
    Event event = createDraftEvent();
    event.publish();

    assertThatThrownBy(event::publish)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only DRAFT events can be published");
  }

  @Test
  @DisplayName("should NOT allow cancelling a non-PUBLISHED event")
  void shouldNotAllowCancellingNonPublishedEvent() {
    Event event = createDraftEvent();

    assertThatThrownBy(event::cancel)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only PUBLISHED events can be cancelled");
  }

  @Test
  @DisplayName("should NOT allow cancelling a CANCELLED event")
  void shouldNotAllowCancellingCancelledEvent() {
    Event event = createDraftEvent();
    event.publish();
    event.cancel();

    assertThatThrownBy(event::cancel)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only PUBLISHED events can be cancelled");
  }

  @Test
  @DisplayName("should update event details when DRAFT")
  void shouldUpdateEventDetailsWhenDraft() {
    Event event = createDraftEvent();
    Instant newDate = now.plusSeconds(172800);

    event.update("New Name", EventCategory.SPORTS, newDate, "New description");

    assertThat(event.name()).isEqualTo("New Name");
    assertThat(event.category()).isEqualTo(EventCategory.SPORTS);
    assertThat(event.eventDate()).isEqualTo(newDate);
    assertThat(event.description()).isEqualTo("New description");
  }

  @Test
  @DisplayName("should update event details when PUBLISHED")
  void shouldUpdateEventDetailsWhenPublished() {
    Event event = createDraftEvent();
    event.publish();
    Instant newDate = now.plusSeconds(172800);

    event.update("Updated Name", EventCategory.THEATER, newDate, "Updated description");

    assertThat(event.name()).isEqualTo("Updated Name");
    assertThat(event.category()).isEqualTo(EventCategory.THEATER);
    assertThat(event.status()).isEqualTo(EventStatus.PUBLISHED);
  }

  @Test
  @DisplayName("should NOT allow updating a CANCELLED event")
  void shouldNotAllowUpdatingCancelledEvent() {
    Event event = createDraftEvent();
    event.publish();
    event.cancel();

    assertThatThrownBy(() -> event.update("New", EventCategory.OTHER, now, "desc"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot update a CANCELLED event");
  }

  @Test
  @DisplayName("should reject null name in constructor")
  void shouldRejectNullName() {
    assertThatThrownBy(() -> new Event(
        eventId, null, EventCategory.CONCERT, now, "desc", organizerId, EventStatus.DRAFT, now, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event name is required");
  }

  @Test
  @DisplayName("should reject blank name in constructor")
  void shouldRejectBlankName() {
    assertThatThrownBy(() -> new Event(
        eventId, "  ", EventCategory.CONCERT, now, "desc", organizerId, EventStatus.DRAFT, now, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event name is required");
  }

  @Test
  @DisplayName("should reject null name in update")
  void shouldRejectNullNameInUpdate() {
    Event event = createDraftEvent();

    assertThatThrownBy(() -> event.update(null, EventCategory.CONCERT, now, "desc"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event name is required");
  }

  @Test
  @DisplayName("should reject null category in constructor")
  void shouldRejectNullCategory() {
    assertThatThrownBy(() -> new Event(
        eventId, "Name", null, now, "desc", organizerId, EventStatus.DRAFT, now, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event category is required");
  }

  @Test
  @DisplayName("should reject null eventDate in constructor")
  void shouldRejectNullEventDate() {
    assertThatThrownBy(() -> new Event(
        eventId, "Name", EventCategory.CONCERT, null, "desc", organizerId, EventStatus.DRAFT, now, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event date is required");
  }

  @Test
  @DisplayName("should reject null status in constructor")
  void shouldRejectNullStatus() {
    assertThatThrownBy(() -> new Event(
        eventId, "Name", EventCategory.CONCERT, now, "desc", organizerId, null, now, now))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event status is required");
  }

  @Test
  @DisplayName("should update timestamp on publish")
  void shouldUpdateTimestampOnPublish() {
    Event event = createDraftEvent();
    Instant beforeUpdate = event.updatedAt();

    event.publish();

    assertThat(event.updatedAt()).isAfterOrEqualTo(beforeUpdate);
  }

  @Test
  @DisplayName("should update timestamp on cancel")
  void shouldUpdateTimestampOnCancel() {
    Event event = createDraftEvent();
    event.publish();
    Instant beforeUpdate = event.updatedAt();

    event.cancel();

    assertThat(event.updatedAt()).isAfterOrEqualTo(beforeUpdate);
  }

  @Test
  @DisplayName("should update timestamp on update")
  void shouldUpdateTimestampOnUpdate() {
    Event event = createDraftEvent();
    Instant beforeUpdate = event.updatedAt();

    event.update("New", EventCategory.OTHER, now, "desc");

    assertThat(event.updatedAt()).isAfterOrEqualTo(beforeUpdate);
  }
}
