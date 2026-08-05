package com.entreck.shared.domain.link;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root representing the association between an Event and a PointOfSale.
 *
 * <p>Per ADR-2, this aggregate belongs to the event context. It carries the
 * association's availability status, an optional note, and the last update
 * timestamp. The aggregate is append-only: the only mutating operation is
 * {@link #changeAvailability(AvailabilityStatus, String, Instant)}.
 *
 * <p>Identity is defined by the composite of {@link EventId} and
 * {@link PointOfSaleId}, enforced as a uniqueness constraint at the
 * persistence layer and checked in the application layer.
 */
public class EventPointOfSaleLink {

  private final LinkId id;
  private final EventId eventId;
  private final PointOfSaleId posId;
  private AvailabilityStatus availabilityStatus;
  private String note;
  private final Instant updatedAt;

  /**
   * Constructs a new EventPointOfSaleLink.
   *
   * @param id the link identifier
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   * @param availabilityStatus the initial availability status
   * @param note an optional note about the link
   * @param updatedAt the last update timestamp
   */
  public EventPointOfSaleLink(
      LinkId id,
      EventId eventId,
      PointOfSaleId posId,
      AvailabilityStatus availabilityStatus,
      String note,
      Instant updatedAt) {
    if (id == null) {
      throw new IllegalArgumentException("Link id is required");
    }
    if (eventId == null) {
      throw new IllegalArgumentException("Event id is required");
    }
    if (posId == null) {
      throw new IllegalArgumentException("PointOfSale id is required");
    }
    if (availabilityStatus == null) {
      throw new IllegalArgumentException("Availability status is required");
    }
    if (updatedAt == null) {
      throw new IllegalArgumentException("Updated at is required");
    }

    this.id = id;
    this.eventId = eventId;
    this.posId = posId;
    this.availabilityStatus = availabilityStatus;
    this.note = note;
    this.updatedAt = updatedAt;
  }

  /**
   * Changes the availability status and optional note for this link.
   *
   * <p>This is the only mutating operation on the aggregate. It enforces the
   * status enum invariant: the new status must be a valid {@link AvailabilityStatus}.
   *
   * @param status the new availability status
   * @param note the optional note describing the availability change
   * @param instant the timestamp of the change
   * @throws IllegalArgumentException if status or instant is null
   */
  public void changeAvailability(AvailabilityStatus status, String note, Instant instant) {
    if (status == null) {
      throw new IllegalArgumentException("Availability status is required");
    }
    if (instant == null) {
      throw new IllegalArgumentException("Instant is required");
    }
    this.availabilityStatus = status;
    this.note = note;
  }

  /** @return the link identifier */
  public LinkId id() {
    return id;
  }

  /** @return the event identifier */
  public EventId eventId() {
    return eventId;
  }

  /** @return the point-of-sale identifier */
  public PointOfSaleId posId() {
    return posId;
  }

  /** @return the current availability status */
  public AvailabilityStatus availabilityStatus() {
    return availabilityStatus;
  }

  /** @return the optional note */
  public String note() {
    return note;
  }

  /** @return the last update timestamp */
  public Instant updatedAt() {
    return updatedAt;
  }
}
