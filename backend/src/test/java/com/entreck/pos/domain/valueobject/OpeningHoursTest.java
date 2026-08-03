package com.entreck.pos.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OpeningHours value object")
class OpeningHoursTest {

  @Test
  @DisplayName("should create valid opening hours")
  void shouldCreateValidOpeningHours() {
    List<WeeklyWindow> windows = List.of(
        new WeeklyWindow(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
    OpeningHours hours = new OpeningHours(windows, "America/New_York");

    assertThat(hours.windows()).hasSize(1);
    assertThat(hours.timezone()).isEqualTo("America/New_York");
  }

  @Test
  @DisplayName("should reject empty windows list")
  void shouldRejectEmptyWindowsList() {
    assertThatThrownBy(() -> new OpeningHours(List.of(), "America/New_York"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("At least one weekly window is required");
  }

  @Test
  @DisplayName("should reject null windows list")
  void shouldRejectNullWindowsList() {
    assertThatThrownBy(() -> new OpeningHours(null, "America/New_York"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("At least one weekly window is required");
  }

  @Test
  @DisplayName("should reject blank timezone")
  void shouldRejectBlankTimezone() {
    List<WeeklyWindow> windows = List.of(
        new WeeklyWindow(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
    assertThatThrownBy(() -> new OpeningHours(windows, ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Timezone is required");
  }
}
