package com.entreck.pos.infrastructure.persistence;

import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.POSStatus;
import com.entreck.pos.domain.valueobject.Address;
import com.entreck.pos.domain.valueobject.Contact;
import com.entreck.pos.domain.valueobject.GeoLocation;
import com.entreck.pos.domain.valueobject.OpeningHours;
import com.entreck.pos.domain.valueobject.WeeklyWindow;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.id.ShopId;
import java.time.DayOfWeek;

/**
 * Static mapper between domain {@link PointOfSale} and {@link PointOfSaleJpaEntity}.
 *
 * <p>Follows ADR-3: static mappers convert between domain objects and JPA
 * entities, keeping the domain layer free of infrastructure dependencies.
 *
 * <p>This class is distinct from the application-layer MapStruct
 * {@code PointOfSaleMapper} which maps domain objects to DTOs.
 */
public final class PointOfSaleMapper {

  private PointOfSaleMapper() {
    // utility class
  }

  /**
   * Converts a JPA entity to a domain PointOfSale.
   *
   * @param entity the JPA entity
   * @return the domain PointOfSale
   */
  public static PointOfSale toDomain(PointOfSaleJpaEntity entity) {
    Address address = new Address(
        entity.getStreet(),
        entity.getCity(),
        entity.getState(),
        entity.getZipCode(),
        entity.getCountry());

    GeoLocation geoLocation = new GeoLocation(
        entity.getLatitude(),
        entity.getLongitude());

    Contact contact = new Contact(
        entity.getEmail(),
        entity.getPhone());

    OpeningHours openingHours = new OpeningHours(
        entity.getOpeningHoursWindows().stream()
            .map(w -> new WeeklyWindow(
                DayOfWeek.valueOf(w.getDayOfWeek()),
                w.getOpen(),
                w.getClose()))
            .toList(),
        entity.getOpeningHoursTimezone());

    return new PointOfSale(
        new PointOfSaleId(entity.getId()),
        entity.getName(),
        address,
        geoLocation,
        contact,
        openingHours,
        new ShopId(entity.getShopId()),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  /**
   * Converts a domain PointOfSale to a JPA entity.
   *
   * <p>The entity ID is set from the domain PointOfSale's ID. For new POS
   * where the adapter wants JPA to auto-generate the ID, the adapter should
   * explicitly set the entity ID to {@code null} after calling this method.
   *
   * @param pos the domain PointOfSale
   * @return the JPA entity
   */
  public static PointOfSaleJpaEntity toJpaEntity(PointOfSale pos) {
    PointOfSaleJpaEntity entity = new PointOfSaleJpaEntity();
    entity.setId(pos.id().value());
    entity.setName(pos.name());
    entity.setStreet(pos.address().street());
    entity.setCity(pos.address().city());
    entity.setState(pos.address().state());
    entity.setZipCode(pos.address().zipCode());
    entity.setCountry(pos.address().country());
    entity.setLatitude(pos.geoLocation().latitude());
    entity.setLongitude(pos.geoLocation().longitude());
    entity.setEmail(pos.contact().email());
    entity.setPhone(pos.contact().phone());
    entity.setOpeningHoursTimezone(pos.openingHours().timezone());
    entity.setOpeningHoursWindows(
        pos.openingHours().windows().stream()
            .map(w -> new WeeklyWindowEmbeddable(
                w.dayOfWeek().name(),
                w.open(),
                w.close()))
            .toList());
    entity.setShopId(pos.shopId().value());
    entity.setStatus(pos.status());
    entity.setCreatedAt(pos.createdAt());
    entity.setUpdatedAt(pos.updatedAt());
    return entity;
  }

  /**
   * Updates the mutable fields of an existing JPA entity from a domain PointOfSale.
   *
   * <p>Used when the adapter detects the entity already exists in the database
   * and needs to merge domain changes into the managed entity.
   *
   * @param entity the existing JPA entity to update
   * @param pos the domain PointOfSale with updated values
   */
  public static void updateEntityFields(PointOfSaleJpaEntity entity, PointOfSale pos) {
    entity.setName(pos.name());
    entity.setStreet(pos.address().street());
    entity.setCity(pos.address().city());
    entity.setState(pos.address().state());
    entity.setZipCode(pos.address().zipCode());
    entity.setCountry(pos.address().country());
    entity.setLatitude(pos.geoLocation().latitude());
    entity.setLongitude(pos.geoLocation().longitude());
    entity.setEmail(pos.contact().email());
    entity.setPhone(pos.contact().phone());
    entity.setOpeningHoursTimezone(pos.openingHours().timezone());
    entity.setOpeningHoursWindows(
        pos.openingHours().windows().stream()
            .map(w -> new WeeklyWindowEmbeddable(
                w.dayOfWeek().name(),
                w.open(),
                w.close()))
            .toList());
    entity.setStatus(pos.status());
    entity.setUpdatedAt(pos.updatedAt());
  }
}
