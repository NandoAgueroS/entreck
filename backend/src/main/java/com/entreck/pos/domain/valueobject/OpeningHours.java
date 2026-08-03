package com.entreck.pos.domain.valueobject;

import java.util.List;

/**
 * Immutable value object representing opening hours.
 *
 * <p>Contains a list of weekly windows and a timezone identifier.
 *
 * @param windows the list of weekly time windows
 * @param timezone the timezone identifier (e.g., "America/New_York")
 */
public record OpeningHours(List<WeeklyWindow> windows, String timezone) {

  public OpeningHours {
    if (windows == null || windows.isEmpty()) {
      throw new IllegalArgumentException("At least one weekly window is required");
    }
    if (timezone == null || timezone.isBlank()) {
      throw new IllegalArgumentException("Timezone is required");
    }
    windows = List.copyOf(windows);
  }
}
