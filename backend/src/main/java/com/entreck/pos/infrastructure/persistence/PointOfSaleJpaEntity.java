package com.entreck.pos.infrastructure.persistence;

import com.entreck.pos.domain.POSStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for persisting PointOfSale aggregates.
 *
 * <p>Maps to the {@code points_of_sale} table. Address, GeoLocation, and
 * Contact value objects are flattened into columns on the main table.
 * OpeningHours windows are stored in a separate {@code pos_opening_hours}
 * table via {@link WeeklyWindowEmbeddable} and {@code @ElementCollection}.
 *
 * <p>Keeps JPA annotations out of the domain layer per Clean Architecture
 * (ADR-3).
 */
@Entity
@Table(name = "points_of_sale")
public class PointOfSaleJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String name;

  // --- Address (flattened) ---

  @Column(name = "street", nullable = false, length = 200)
  private String street;

  @Column(name = "city", nullable = false, length = 100)
  private String city;

  @Column(name = "state", nullable = false, length = 100)
  private String state;

  @Column(name = "zip_code", nullable = false, length = 20)
  private String zipCode;

  @Column(name = "country", nullable = false, length = 2)
  private String country;

  // --- GeoLocation (flattened) ---

  @Column(name = "latitude", nullable = false)
  private Double latitude;

  @Column(name = "longitude", nullable = false)
  private Double longitude;

  // --- Contact (flattened) ---

  @Column(name = "email", nullable = false, length = 200)
  private String email;

  @Column(name = "phone", nullable = false, length = 50)
  private String phone;

  // --- OpeningHours ---

  @Column(name = "opening_hours_timezone", nullable = false, length = 50)
  private String openingHoursTimezone;

  @ElementCollection
  @CollectionTable(
      name = "pos_opening_hours",
      joinColumns = @JoinColumn(name = "pos_id"))
  @OrderColumn(name = "sort_order")
  private List<WeeklyWindowEmbeddable> openingHoursWindows = new ArrayList<>();

  // --- Other fields ---

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private POSStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /** Default constructor for JPA. */
  protected PointOfSaleJpaEntity() {}

  /**
   * Constructs a fully populated JPA entity.
   *
   * @param id the point of sale identifier
   * @param name the point of sale name
   * @param street the street address
   * @param city the city
   * @param state the state or province
   * @param zipCode the postal code
   * @param country the ISO-3166 alpha-2 country code
   * @param latitude the latitude in decimal degrees
   * @param longitude the longitude in decimal degrees
   * @param email the contact email
   * @param phone the contact phone
   * @param openingHoursTimezone the opening hours timezone
   * @param openingHoursWindows the weekly time windows
   * @param shopId the shop identifier
   * @param status the POS status
   * @param createdAt the creation timestamp
   * @param updatedAt the last update timestamp
   */
  public PointOfSaleJpaEntity(
      Long id,
      String name,
      String street,
      String city,
      String state,
      String zipCode,
      String country,
      Double latitude,
      Double longitude,
      String email,
      String phone,
      String openingHoursTimezone,
      List<WeeklyWindowEmbeddable> openingHoursWindows,
      Long shopId,
      POSStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.name = name;
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.country = country;
    this.latitude = latitude;
    this.longitude = longitude;
    this.email = email;
    this.phone = phone;
    this.openingHoursTimezone = openingHoursTimezone;
    this.openingHoursWindows = openingHoursWindows != null
        ? new ArrayList<>(openingHoursWindows)
        : new ArrayList<>();
    this.shopId = shopId;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getCountry() {
    return country;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getOpeningHoursTimezone() {
    return openingHoursTimezone;
  }

  public List<WeeklyWindowEmbeddable> getOpeningHoursWindows() {
    return openingHoursWindows;
  }

  public Long getShopId() {
    return shopId;
  }

  public POSStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  // --- Setters (used by infrastructure adapter and mappers) ---

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setOpeningHoursTimezone(String openingHoursTimezone) {
    this.openingHoursTimezone = openingHoursTimezone;
  }

  public void setOpeningHoursWindows(List<WeeklyWindowEmbeddable> openingHoursWindows) {
    this.openingHoursWindows = openingHoursWindows != null
        ? new ArrayList<>(openingHoursWindows)
        : new ArrayList<>();
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setStatus(POSStatus status) {
    this.status = status;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
