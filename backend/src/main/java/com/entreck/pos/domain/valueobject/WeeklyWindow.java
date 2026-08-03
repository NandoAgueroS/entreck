package com.entreck.pos.domain.valueobject;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Immutable value object representing a weekly time window.
 *
 * <p>Validates that open time is strictly before close time.
 *
 * @param dayOfWeek the day of the week
 * @param open the opening time
 * @param close the closing time
 */
public record WeeklyWindow(DayOfWeek dayOfWeek, LocalTime open, LocalTime close) {

  public WeeklyWindow {
    if (dayOfWeek == null) {
      throw new IllegalArgumentException("Day of week is required");
    }
    if (open == null) {
      throw new IllegalArgumentException("Open time is required");
    }
    if (close == null) {
      throw new IllegalArgumentException("Close time is required");
    }
    if (!open.isBefore(close)) {
      throw new IllegalArgumentException(
          "Open time must be before close time: " + open + " >= " + close);
    }
  }
}
