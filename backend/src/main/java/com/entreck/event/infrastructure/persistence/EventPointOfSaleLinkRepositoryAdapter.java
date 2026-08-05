package com.entreck.event.infrastructure.persistence;

import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * JPA adapter implementing the domain {@link EventPointOfSaleLinkRepository} port.
 *
 * <p>Translates between domain types and JPA entities using the static
 * {@link EventPointOfSaleLinkMapper}. New links (where the domain ID does not
 * yet exist in the database) are persisted with a null JPA ID so that
 * PostgreSQL generates the identifier via the {@code IDENTITY} strategy.
 */
@Repository
public class EventPointOfSaleLinkRepositoryAdapter implements EventPointOfSaleLinkRepository {

  private final SpringDataEventPointOfSaleLinkRepository springDataRepo;

  /**
   * Constructs the adapter with the Spring Data repository.
   *
   * @param springDataRepo the Spring Data JPA repository
   */
  public EventPointOfSaleLinkRepositoryAdapter(
      SpringDataEventPointOfSaleLinkRepository springDataRepo) {
    this.springDataRepo = springDataRepo;
  }

  /** {@inheritDoc} */
  @Override
  public EventPointOfSaleLink save(EventPointOfSaleLink link) {
    EventPointOfSaleLinkJpaEntity entity;
    if (springDataRepo.existsById(link.id().value())) {
      entity = springDataRepo.findById(link.id().value()).orElseThrow();
      EventPointOfSaleLinkMapper.updateEntityFields(entity, link);
    } else {
      entity = EventPointOfSaleLinkMapper.toJpaEntity(link);
      entity.setId(null); // let JPA auto-generate
    }
    EventPointOfSaleLinkJpaEntity saved = springDataRepo.save(entity);
    return EventPointOfSaleLinkMapper.toDomain(saved);
  }

  /** {@inheritDoc} */
  @Override
  public List<EventPointOfSaleLink> findByEventId(EventId eventId) {
    return springDataRepo.findByEventId(eventId.value()).stream()
        .map(EventPointOfSaleLinkMapper::toDomain)
        .toList();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<EventPointOfSaleLink> findByEventIdAndPosId(
      EventId eventId, PointOfSaleId posId) {
    return springDataRepo.findByEventIdAndPosId(eventId.value(), posId.value())
        .map(EventPointOfSaleLinkMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByEventIdAndPosId(EventId eventId, PointOfSaleId posId) {
    return springDataRepo.existsByEventIdAndPosId(eventId.value(), posId.value());
  }

  /** {@inheritDoc} */
  @Override
  public void delete(EventId eventId, PointOfSaleId posId) {
    springDataRepo.deleteByEventIdAndPosId(eventId.value(), posId.value());
  }
}
