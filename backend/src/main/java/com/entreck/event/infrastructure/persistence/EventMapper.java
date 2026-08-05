package com.entreck.event.infrastructure.persistence;

import com.entreck.event.domain.Event;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;

/**
 * Static mapper between domain {@link Event} and {@link EventJpaEntity}.
 *
 * <p>Follows ADR-3: static mappers convert between domain objects and JPA
 * entities, keeping the domain layer free of infrastructure dependencies.
 *
 * <p>This class is distinct from the application-layer MapStruct
 * {@code EventMapper} which maps domain objects to DTOs.
 */
public final class EventMapper {

  private EventMapper() {
    // utility class
  }

  /**
   * Converts a JPA entity to a domain Event.
   *
   * @param entity the JPA entity
   * @return the domain Event
   */
  public static Event toDomain(EventJpaEntity entity) {
    return new Event(
        new EventId(entity.getId()),
        entity.getName(),
        entity.getCategory(),
        entity.getEventDate(),
        entity.getDescription(),
        new OrganizerId(entity.getOrganizerId()),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  /**
   * Converts a domain Event to a JPA entity.
   *
   * <p>The entity ID is set from the domain Event's ID. For new events where
   * the adapter wants JPA to auto-generate the ID, the adapter should
   * explicitly set the entity ID to {@code null} after calling this method.
   *
   * @param event the domain Event
   * @return the JPA entity
   */
  public static EventJpaEntity toJpaEntity(Event event) {
    EventJpaEntity entity = new EventJpaEntity();
    entity.setId(event.id().value());
    entity.setName(event.name());
    entity.setCategory(event.category());
    entity.setEventDate(event.eventDate());
    entity.setDescription(event.description());
    entity.setOrganizerId(event.organizerId().value());
    entity.setStatus(event.status());
    entity.setCreatedAt(event.createdAt());
    entity.setUpdatedAt(event.updatedAt());
    return entity;
  }

  /**
   * Updates the mutable fields of an existing JPA entity from a domain Event.
   *
   * <p>Used when the adapter detects the entity already exists in the database
   * and needs to merge domain changes into the managed entity.
   *
   * @param entity the existing JPA entity to update
   * @param event the domain Event with updated values
   */
  public static void updateEntityFields(EventJpaEntity entity, Event event) {
    entity.setName(event.name());
    entity.setCategory(event.category());
    entity.setEventDate(event.eventDate());
    entity.setDescription(event.description());
    entity.setStatus(event.status());
    entity.setUpdatedAt(event.updatedAt());
  }
}
