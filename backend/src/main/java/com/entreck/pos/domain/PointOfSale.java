package com.entreck.pos.domain;

import com.entreck.pos.domain.valueobject.Address;
import com.entreck.pos.domain.valueobject.Contact;
import com.entreck.pos.domain.valueobject.GeoLocation;
import com.entreck.pos.domain.valueobject.OpeningHours;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.id.ShopId;
import java.time.Instant;

/**
 * Aggregate root for the PointOfSale bounded context.
 *
 * <p>A PointOfSale represents a physical shop authorized to sell tickets for
 * one or more events. It has address, contact, location, and opening hours.
 *
 * <p>Updates are supported via {@link #update} method (PATCH semantics).
 */
public class PointOfSale {

  private final PointOfSaleId id;
  private String name;
  private Address address;
  private GeoLocation geoLocation;
  private Contact contact;
  private OpeningHours openingHours;
  private final ShopId shopId;
  private POSStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  /**
   * Constructs a new PointOfSale in ACTIVE status.
   *
   * @param id the point of sale identifier
   * @param name the point of sale name
   * @param address the physical address
   * @param geoLocation the geographic coordinates
   * @param contact the contact information
   * @param openingHours the opening hours
   * @param shopId the shop identifier
   * @param createdAt the creation timestamp
   * @param updatedAt the last update timestamp
   */
  public PointOfSale(
      PointOfSaleId id,
      String name,
      Address address,
      GeoLocation geoLocation,
      Contact contact,
      OpeningHours openingHours,
      ShopId shopId,
      Instant createdAt,
      Instant updatedAt) {
    if (id == null) {
      throw new IllegalArgumentException("PointOfSale id is required");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("PointOfSale name is required");
    }
    if (address == null) {
      throw new IllegalArgumentException("Address is required");
    }
    if (geoLocation == null) {
      throw new IllegalArgumentException("GeoLocation is required");
    }
    if (contact == null) {
      throw new IllegalArgumentException("Contact is required");
    }
    if (openingHours == null) {
      throw new IllegalArgumentException("OpeningHours is required");
    }
    if (shopId == null) {
      throw new IllegalArgumentException("Shop id is required");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("Created at is required");
    }
    if (updatedAt == null) {
      throw new IllegalArgumentException("Updated at is required");
    }

    this.id = id;
    this.name = name;
    this.address = address;
    this.geoLocation = geoLocation;
    this.contact = contact;
    this.openingHours = openingHours;
    this.shopId = shopId;
    this.status = POSStatus.ACTIVE;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Constructs a PointOfSale with an explicit status (for rehydration from persistence).
   *
   * @param id the point of sale identifier
   * @param name the point of sale name
   * @param address the physical address
   * @param geoLocation the geographic coordinates
   * @param contact the contact information
   * @param openingHours the opening hours
   * @param shopId the shop identifier
   * @param status the POS status
   * @param createdAt the creation timestamp
   * @param updatedAt the last update timestamp
   */
  public PointOfSale(
      PointOfSaleId id,
      String name,
      Address address,
      GeoLocation geoLocation,
      Contact contact,
      OpeningHours openingHours,
      ShopId shopId,
      POSStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this(
        id,
        name,
        address,
        geoLocation,
        contact,
        openingHours,
        shopId,
        createdAt,
        updatedAt);
    if (status == null) {
      throw new IllegalArgumentException("POS status is required");
    }
    this.status = status;
  }

  /**
   * Updates the point of sale details (PATCH semantics).
   *
   * <p>Only non-null fields are updated.
   *
   * @param name the new name, or null to leave unchanged
   * @param address the new address, or null to leave unchanged
   * @param geoLocation the new geo location, or null to leave unchanged
   * @param contact the new contact, or null to leave unchanged
   * @param openingHours the new opening hours, or null to leave unchanged
   */
  public void update(
      String name,
      Address address,
      GeoLocation geoLocation,
      Contact contact,
      OpeningHours openingHours) {
    if (name != null && !name.isBlank()) {
      this.name = name;
    }
    if (address != null) {
      this.address = address;
    }
    if (geoLocation != null) {
      this.geoLocation = geoLocation;
    }
    if (contact != null) {
      this.contact = contact;
    }
    if (openingHours != null) {
      this.openingHours = openingHours;
    }
    this.updatedAt = Instant.now();
  }

  public PointOfSaleId id() {
    return id;
  }

  public String name() {
    return name;
  }

  public Address address() {
    return address;
  }

  public GeoLocation geoLocation() {
    return geoLocation;
  }

  public Contact contact() {
    return contact;
  }

  public OpeningHours openingHours() {
    return openingHours;
  }

  public ShopId shopId() {
    return shopId;
  }

  public POSStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
