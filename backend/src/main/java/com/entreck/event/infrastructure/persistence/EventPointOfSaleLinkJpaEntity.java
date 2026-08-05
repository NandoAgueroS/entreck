package com.entreck.event.infrastructure.persistence;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity for persisting EventPointOfSaleLink aggregates.
 *
 * <p>Maps to the {@code event_pos_link} table. Keeps JPA annotations out of the
 * domain layer per Clean Architecture (ADR-3). The {@code (event_id, pos_id)}
 * pair has a unique constraint as a second line of defense against duplicate links.
 */
@Entity
@Table(
    name = "event_pos_link",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_event_pos_link_event_pos",
        columnNames = {"event_id", "pos_id"}))
public class EventPointOfSaleLinkJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id", nullable = false)
  private Long eventId;

  @Column(name = "pos_id", nullable = false)
  private Long posId;

  @Enumerated(EnumType.STRING)
  @Column(name = "availability_status", nullable = false, length = 50)
  private AvailabilityStatus availabilityStatus;

  @Column(length = 500)
  private String note;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /** Default constructor for JPA. */
  protected EventPointOfSaleLinkJpaEntity() {}

  /**
   * Constructs a fully populated JPA entity.
   *
   * @param id the link identifier
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   * @param availabilityStatus the availability status
   * @param note the optional note
   * @param updatedAt the last update timestamp
   */
  public EventPointOfSaleLinkJpaEntity(
      Long id,
      Long eventId,
      Long posId,
      AvailabilityStatus availabilityStatus,
      String note,
      Instant updatedAt) {
    this.id = id;
    this.eventId = eventId;
    this.posId = posId;
    this.availabilityStatus = availabilityStatus;
    this.note = note;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public Long getEventId() {
    return eventId;
  }

  public Long getPosId() {
    return posId;
  }

  public AvailabilityStatus getAvailabilityStatus() {
    return availabilityStatus;
  }

  public String getNote() {
    return note;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  // --- Setters (used by infrastructure adapter and mappers) ---

  public void setId(Long id) {
    this.id = id;
  }

  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }

  public void setPosId(Long posId) {
    this.posId = posId;
  }

  public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
