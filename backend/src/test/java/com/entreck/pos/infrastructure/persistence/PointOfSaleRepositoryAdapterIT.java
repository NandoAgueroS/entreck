package com.entreck.pos.infrastructure.persistence;

import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.POSStatus;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.pos.domain.valueobject.Address;
import com.entreck.pos.domain.valueobject.Contact;
import com.entreck.pos.domain.valueobject.GeoLocation;
import com.entreck.pos.domain.valueobject.OpeningHours;
import com.entreck.pos.domain.valueobject.WeeklyWindow;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.id.ShopId;
import com.entreck.shared.testing.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link PointOfSaleRepositoryAdapter} using Testcontainers.
 *
 * <p>Verifies the full persistence flow: domain → JPA entity → PostgreSQL →
 * JPA entity → domain. Uses a real PostgreSQL 16 instance via Testcontainers
 * to validate JPA mappings, embedded value object round-trips, and latitude/
 * longitude range persistence.
 */
@DataJpaTest
@Import(PointOfSaleRepositoryAdapter.class)
class PointOfSaleRepositoryAdapterIT extends BaseIntegrationTest {

  @Autowired
  private PointOfSaleRepository pointOfSaleRepository;

  @Autowired
  private SpringDataPointOfSaleRepository springDataRepo;

  @Test
  void saveAndFindById_roundTrip() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PointOfSale pos = createTestPos("Ticket Shop", "123 Main St", -34.6037, -58.3816);

    PointOfSale saved = pointOfSaleRepository.save(pos);

    assertThat(saved.id().value()).isNotNull().isPositive();
    assertThat(saved.name()).isEqualTo("Ticket Shop");
    assertThat(saved.status()).isEqualTo(POSStatus.ACTIVE);
    assertThat(saved.shopId().value()).isEqualTo(10L);

    Optional<PointOfSale> found = pointOfSaleRepository.findById(saved.id());
    assertThat(found).isPresent();
    assertThat(found.get().name()).isEqualTo("Ticket Shop");
  }

  @Test
  void save_existingPos_updatesFields() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PointOfSale pos = createTestPos("Original Shop", "456 Oak Ave", -34.5, -58.4);
    PointOfSale saved = pointOfSaleRepository.save(pos);

    saved.update("Updated Shop", null, null, null, null);
    PointOfSale updated = pointOfSaleRepository.save(saved);

    assertThat(updated.name()).isEqualTo("Updated Shop");
    assertThat(updated.id()).isEqualTo(saved.id());
  }

  @Test
  void save_persistsAddressValueObject() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PointOfSale pos = createTestPos("Address Test", "789 Pine Rd", -34.6, -58.3);

    PointOfSale saved = pointOfSaleRepository.save(pos);
    Optional<PointOfSale> found = pointOfSaleRepository.findById(saved.id());

    assertThat(found).isPresent();
    Address addr = found.get().address();
    assertThat(addr.street()).isEqualTo("789 Pine Rd");
    assertThat(addr.city()).isEqualTo("Buenos Aires");
    assertThat(addr.country()).isEqualTo("AR");
  }

  @Test
  void save_persistsGeoLocationValueObject() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PointOfSale pos = createTestPos("Geo Test", "100 Geo St", -34.6037, -58.3816);

    PointOfSale saved = pointOfSaleRepository.save(pos);
    Optional<PointOfSale> found = pointOfSaleRepository.findById(saved.id());

    assertThat(found).isPresent();
    GeoLocation geo = found.get().geoLocation();
    assertThat(geo.latitude()).isBetween(-90.0, 90.0);
    assertThat(geo.longitude()).isBetween(-180.0, 180.0);
    assertThat(geo.latitude()).isEqualTo(-34.6037);
    assertThat(geo.longitude()).isEqualTo(-58.3816);
  }

  @Test
  void save_persistsContactValueObject() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PointOfSale pos = createTestPos("Contact Test", "200 Contact St", 0.0, 0.0);

    PointOfSale saved = pointOfSaleRepository.save(pos);
    Optional<PointOfSale> found = pointOfSaleRepository.findById(saved.id());

    assertThat(found).isPresent();
    Contact contact = found.get().contact();
    assertThat(contact.email()).isEqualTo("shop@test.com");
    assertThat(contact.phone()).isEqualTo("+54 11 1234-5678");
  }

  @Test
  void save_persistsOpeningHoursValueObject() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PointOfSale pos = createTestPos("Hours Test", "300 Hours St", 10.0, 20.0);

    PointOfSale saved = pointOfSaleRepository.save(pos);
    Optional<PointOfSale> found = pointOfSaleRepository.findById(saved.id());

    assertThat(found).isPresent();
    OpeningHours hours = found.get().openingHours();
    assertThat(hours.timezone()).isEqualTo("America/Argentina/Buenos_Aires");
    assertThat(hours.windows()).hasSize(2);
    assertThat(hours.windows().get(0).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    assertThat(hours.windows().get(0).open()).isEqualTo(LocalTime.of(9, 0));
    assertThat(hours.windows().get(0).close()).isEqualTo(LocalTime.of(18, 0));
  }

  @Test
  void existsById_returnsTrueForExisting() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PointOfSale pos = createTestPos("Exist Test", "400 Exist St", 0.0, 0.0);
    PointOfSale saved = pointOfSaleRepository.save(pos);

    assertThat(pointOfSaleRepository.existsById(saved.id())).isTrue();
    assertThat(pointOfSaleRepository.existsById(new PointOfSaleId(99999L))).isFalse();
  }

  /**
   * Helper to save a test POS with auto-generated ID via adapter.
   *
   * @param name the POS name
   * @param street the street address
   * @param lat the latitude
   * @param lon the longitude
   * @return the saved POS with real ID
   */
  private PointOfSale createTestPos(String name, String street, double lat, double lon) {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    return new PointOfSale(
        new PointOfSaleId(0L),
        name,
        new Address(street, "Buenos Aires", "BA", "C1000", "AR"),
        new GeoLocation(lat, lon),
        new Contact("shop@test.com", "+54 11 1234-5678"),
        new OpeningHours(
            List.of(
                new WeeklyWindow(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
                new WeeklyWindow(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0))),
            "America/Argentina/Buenos_Aires"),
        new ShopId(10L),
        now,
        now);
  }
}
