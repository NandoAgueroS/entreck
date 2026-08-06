package com.entreck.pos.application.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for opening hours data.
 *
 * @param windows the list of weekly time windows
 * @param timezone the timezone identifier
 */
public record OpeningHoursDto(
    List<WeeklyWindowDto> windows,

    @NotBlank(message = "Timezone is required")
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    String timezone) {

  /**
   * DTO for a single weekly time window.
   *
   * @param dayOfWeek the day of the week
   * @param open the opening time
   * @param close the closing time
   */
  public record WeeklyWindowDto(DayOfWeek dayOfWeek, LocalTime open, LocalTime close) {}
}
