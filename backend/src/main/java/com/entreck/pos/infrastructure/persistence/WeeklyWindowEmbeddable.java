package com.entreck.pos.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalTime;

/**
 * JPA embeddable representing a single weekly time window.
 *
 * <p>Used by {@link PointOfSaleJpaEntity} in an {@code @ElementCollection}
 * to persist {@link com.entreck.pos.domain.valueobject.OpeningHours} windows
 * in a separate table. Keeps JPA annotations out of the domain layer per
 * Clean Architecture (ADR-3).
 */
@Embeddable
public class WeeklyWindowEmbeddable {

  @Column(name = "day_of_week", nullable = false, length = 20)
  private String dayOfWeek;

  @Column(name = "open_time", nullable = false)
  private LocalTime open;

  @Column(name = "close_time", nullable = false)
  private LocalTime close;

  /** Default constructor for JPA. */
  protected WeeklyWindowEmbeddable() {}

  /**
   * Constructs a weekly window embeddable.
   *
   * @param dayOfWeek the day of the week as a string (e.g. {@code "MONDAY"})
   * @param open the opening time
   * @param close the closing time
   */
  public WeeklyWindowEmbeddable(String dayOfWeek, LocalTime open, LocalTime close) {
    this.dayOfWeek = dayOfWeek;
    this.open = open;
    this.close = close;
  }

  /**
   * Returns the day of the week.
   *
   * @return the day name
   */
  public String getDayOfWeek() {
    return dayOfWeek;
  }

  /**
   * Returns the opening time.
   *
   * @return the opening time
   */
  public LocalTime getOpen() {
    return open;
  }

  /**
   * Returns the closing time.
   *
   * @return the closing time
   */
  public LocalTime getClose() {
    return close;
  }
}
