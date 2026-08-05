package com.entreck.event.infrastructure.persistence;

import com.entreck.shared.domain.enums.EventCategory;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity for persisting Event aggregates.
 *
 * <p>Maps to the {@code events} table. Keeps JPA annotations out of the domain
 * layer per Clean Architecture (ADR-3).
 */
@Entity
@Table(name = "events", uniqueConstraints = @UniqueConstraint(name = "uq_events_name", columnNames = "name"))
public class EventJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EventCategory category;

  @Column(name = "event_date", nullable = false)
  private Instant eventDate;

  @Column(length = 2000)
  private String description;

  @Column(name = "organizer_id", nullable = false)
  private Long organizerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private com.entreck.event.domain.EventStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /** Default constructor for JPA. */
  protected EventJpaEntity() {}

  /**
   * Constructs a fully populated JPA entity.
   *
   * @param id the event identifier
   * @param name the event name
   * @param category the event category
   * @param eventDate the event date
   * @param description the event description
   * @param organizerId the organizer identifier
   * @param status the event status
   * @param createdAt the creation timestamp
   * @param updatedAt the last update timestamp
   */
  public EventJpaEntity(
      Long id,
      String name,
      EventCategory category,
      Instant eventDate,
      String description,
      Long organizerId,
      com.entreck.event.domain.EventStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.eventDate = eventDate;
    this.description = description;
    this.organizerId = organizerId;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public EventCategory getCategory() {
    return category;
  }

  public Instant getEventDate() {
    return eventDate;
  }

  public String getDescription() {
    return description;
  }

  public Long getOrganizerId() {
    return organizerId;
  }

  public com.entreck.event.domain.EventStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  // --- Setters (used by infrastructure adapter and mappers) ---

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCategory(EventCategory category) {
    this.category = category;
  }

  public void setEventDate(Instant eventDate) {
    this.eventDate = eventDate;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setOrganizerId(Long organizerId) {
    this.organizerId = organizerId;
  }

  public void setStatus(com.entreck.event.domain.EventStatus status) {
    this.status = status;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
