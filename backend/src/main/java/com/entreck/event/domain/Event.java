package com.entreck.event.domain;

import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import java.time.Instant;

/**
 * Aggregate root for the Event bounded context.
 *
 * <p>An Event represents a ticketed event (concert, sports game, theater
 * performance, etc.) organized by an organizer. Events have a lifecycle:
 * {@code DRAFT → PUBLISHED → CANCELLED}.
 *
 * <p>State transitions are enforced via domain methods:
 * <ul>
 *   <li>{@link #publish()} — transitions from DRAFT to PUBLISHED</li>
 *   <li>{@link #cancel()} — transitions from PUBLISHED to CANCELLED</li>
 *   <li>{@link #update(String, EventCategory, Instant, String)} — updates event
 *       details; only allowed when status is DRAFT or PUBLISHED</li>
 * </ul>
 */
public class Event {

  private final EventId id;
  private String name;
  private EventCategory category;
  private Instant eventDate;
  private String description;
  private final OrganizerId organizerId;
  private EventStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  /**
   * Constructs a new Event in DRAFT status.
   *
   * @param id the event identifier
   * @param name the event name
   * @param category the event category
   * @param eventDate the date and time of the event
   * @param description the event description
   * @param organizerId the organizer identifier
   * @param createdAt the creation timestamp
   * @param updatedAt the last update timestamp
   */
  public Event(
      EventId id,
      String name,
      EventCategory category,
      Instant eventDate,
      String description,
      OrganizerId organizerId,
      Instant createdAt,
      Instant updatedAt) {
    if (id == null) {
      throw new IllegalArgumentException("Event id is required");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Event name is required");
    }
    if (category == null) {
      throw new IllegalArgumentException("Event category is required");
    }
    if (eventDate == null) {
      throw new IllegalArgumentException("Event date is required");
    }
    if (organizerId == null) {
      throw new IllegalArgumentException("Organizer id is required");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("Created at is required");
    }
    if (updatedAt == null) {
      throw new IllegalArgumentException("Updated at is required");
    }

    this.id = id;
    this.name = name;
    this.category = category;
    this.eventDate = eventDate;
    this.description = description;
    this.organizerId = organizerId;
    this.status = EventStatus.DRAFT;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Constructs an Event with an explicit status (for rehydration from persistence).
   *
   * @param id the event identifier
   * @param name the event name
   * @param category the event category
   * @param eventDate the date and time of the event
   * @param description the event description
   * @param organizerId the organizer identifier
   * @param status the event status
   * @param createdAt the creation timestamp
   * @param updatedAt the last update timestamp
   */
  public Event(
      EventId id,
      String name,
      EventCategory category,
      Instant eventDate,
      String description,
      OrganizerId organizerId,
      EventStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this(
        id,
        name,
        category,
        eventDate,
        description,
        organizerId,
        createdAt,
        updatedAt);
    if (status == null) {
      throw new IllegalArgumentException("Event status is required");
    }
    this.status = status;
  }

  /**
   * Publishes the event, transitioning from DRAFT to PUBLISHED.
   *
   * @throws IllegalStateException if the event is not in DRAFT status
   */
  public void publish() {
    if (status != EventStatus.DRAFT) {
      throw new IllegalStateException(
          "Only DRAFT events can be published. Current status: " + status);
    }
    this.status = EventStatus.PUBLISHED;
    this.updatedAt = Instant.now();
  }

  /**
   * Cancels the event, transitioning from PUBLISHED to CANCELLED.
   *
   * @throws IllegalStateException if the event is not in PUBLISHED status
   */
  public void cancel() {
    if (status != EventStatus.PUBLISHED) {
      throw new IllegalStateException(
          "Only PUBLISHED events can be cancelled. Current status: " + status);
    }
    this.status = EventStatus.CANCELLED;
    this.updatedAt = Instant.now();
  }

  /**
   * Updates the event details.
   *
   * <p>Updates are only allowed when the event is in DRAFT or PUBLISHED status.
   * Cancelled events cannot be modified.
   *
   * @param name the new event name
   * @param category the new event category
   * @param eventDate the new event date
   * @param description the new event description
   * @throws IllegalStateException if the event is CANCELLED
   */
  public void update(
      String name, EventCategory category, Instant eventDate, String description) {
    if (status == EventStatus.CANCELLED) {
      throw new IllegalStateException("Cannot update a CANCELLED event");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Event name is required");
    }
    if (category == null) {
      throw new IllegalArgumentException("Event category is required");
    }
    if (eventDate == null) {
      throw new IllegalArgumentException("Event date is required");
    }

    this.name = name;
    this.category = category;
    this.eventDate = eventDate;
    this.description = description;
    this.updatedAt = Instant.now();
  }

  public EventId id() {
    return id;
  }

  public String name() {
    return name;
  }

  public EventCategory category() {
    return category;
  }

  public Instant eventDate() {
    return eventDate;
  }

  public String description() {
    return description;
  }

  public OrganizerId organizerId() {
    return organizerId;
  }

  public EventStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
