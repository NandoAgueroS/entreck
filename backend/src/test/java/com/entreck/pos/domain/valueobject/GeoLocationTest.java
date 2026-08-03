package com.entreck.pos.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GeoLocation value object")
class GeoLocationTest {

  @Test
  @DisplayName("should create valid geo location")
  void shouldCreateValidGeoLocation() {
    GeoLocation geo = new GeoLocation(40.7128, -74.0060);

    assertThat(geo.latitude()).isEqualTo(40.7128);
    assertThat(geo.longitude()).isEqualTo(-74.0060);
  }

  @Test
  @DisplayName("should reject latitude above 90")
  void shouldRejectLatitudeAbove90() {
    assertThatThrownBy(() -> new GeoLocation(91.0, 0.0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Latitude must be between -90 and 90");
  }

  @Test
  @DisplayName("should reject latitude below -90")
  void shouldRejectLatitudeBelowMinus90() {
    assertThatThrownBy(() -> new GeoLocation(-91.0, 0.0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Latitude must be between -90 and 90");
  }

  @Test
  @DisplayName("should reject longitude above 180")
  void shouldRejectLongitudeAbove180() {
    assertThatThrownBy(() -> new GeoLocation(0.0, 181.0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Longitude must be between -180 and 180");
  }

  @Test
  @DisplayName("should reject longitude below -180")
  void shouldRejectLongitudeBelowMinus180() {
    assertThatThrownBy(() -> new GeoLocation(0.0, -181.0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Longitude must be between -180 and 180");
  }

  @Test
  @DisplayName("should accept boundary values")
  void shouldAcceptBoundaryValues() {
    assertThat(new GeoLocation(90.0, 180.0)).isNotNull();
    assertThat(new GeoLocation(-90.0, -180.0)).isNotNull();
    assertThat(new GeoLocation(0.0, 0.0)).isNotNull();
  }
}
