package com.entreck.pos.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("WeeklyWindow value object")
class WeeklyWindowTest {

  @Test
  @DisplayName("should create valid weekly window")
  void shouldCreateValidWeeklyWindow() {
    WeeklyWindow window = new WeeklyWindow(
        DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

    assertThat(window.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    assertThat(window.open()).isEqualTo(LocalTime.of(9, 0));
    assertThat(window.close()).isEqualTo(LocalTime.of(17, 0));
  }

  @Test
  @DisplayName("should reject open equal to close")
  void shouldRejectOpenEqualToClose() {
    assertThatThrownBy(() -> new WeeklyWindow(
        DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(9, 0)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Open time must be before close time");
  }

  @Test
  @DisplayName("should reject open after close")
  void shouldRejectOpenAfterClose() {
    assertThatThrownBy(() -> new WeeklyWindow(
        DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(9, 0)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Open time must be before close time");
  }

  @Test
  @DisplayName("should reject null day of week")
  void shouldRejectNullDayOfWeek() {
    assertThatThrownBy(() -> new WeeklyWindow(
        null, LocalTime.of(9, 0), LocalTime.of(17, 0)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Day of week is required");
  }
}
