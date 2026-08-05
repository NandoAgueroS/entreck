package com.entreck.event.infrastructure.persistence;

import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import com.entreck.shared.domain.link.LinkId;

/**
 * Static mapper between domain {@link EventPointOfSaleLink} and
 * {@link EventPointOfSaleLinkJpaEntity}.
 *
 * <p>Follows ADR-3: static mappers convert between domain objects and JPA
 * entities, keeping the domain layer free of infrastructure dependencies.
 */
public final class EventPointOfSaleLinkMapper {

  private EventPointOfSaleLinkMapper() {
    // utility class
  }

  /**
   * Converts a JPA entity to a domain EventPointOfSaleLink.
   *
   * @param entity the JPA entity
   * @return the domain EventPointOfSaleLink
   */
  public static EventPointOfSaleLink toDomain(EventPointOfSaleLinkJpaEntity entity) {
    return new EventPointOfSaleLink(
        new LinkId(entity.getId()),
        new EventId(entity.getEventId()),
        new PointOfSaleId(entity.getPosId()),
        entity.getAvailabilityStatus(),
        entity.getNote(),
        entity.getUpdatedAt());
  }

  /**
   * Converts a domain EventPointOfSaleLink to a JPA entity.
   *
   * <p>The entity ID is set from the domain link's ID. For new links where
   * the adapter wants JPA to auto-generate the ID, the adapter should
   * explicitly set the entity ID to {@code null} after calling this method.
   *
   * @param link the domain EventPointOfSaleLink
   * @return the JPA entity
   */
  public static EventPointOfSaleLinkJpaEntity toJpaEntity(EventPointOfSaleLink link) {
    EventPointOfSaleLinkJpaEntity entity = new EventPointOfSaleLinkJpaEntity();
    entity.setId(link.id().value());
    entity.setEventId(link.eventId().value());
    entity.setPosId(link.posId().value());
    entity.setAvailabilityStatus(link.availabilityStatus());
    entity.setNote(link.note());
    entity.setUpdatedAt(link.updatedAt());
    return entity;
  }

  /**
   * Updates the mutable fields of an existing JPA entity from a domain link.
   *
   * <p>Used when the adapter detects the entity already exists in the database
   * and needs to merge domain changes into the managed entity.
   *
   * @param entity the existing JPA entity to update
   * @param link the domain EventPointOfSaleLink with updated values
   */
  public static void updateEntityFields(
      EventPointOfSaleLinkJpaEntity entity, EventPointOfSaleLink link) {
    entity.setAvailabilityStatus(link.availabilityStatus());
    entity.setNote(link.note());
    entity.setUpdatedAt(link.updatedAt());
  }
}
