package com.entreck.event.infrastructure.persistence;

import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventSearchCriteria;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.PageMeta;
import com.entreck.shared.domain.id.EventId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the domain {@link EventRepository} port.
 *
 * <p>Translates between domain types and JPA entities using the static
 * {@link EventMapper}. New events (where the domain ID does not yet exist in
 * the database) are persisted with a null JPA ID so that PostgreSQL generates
 * the identifier via the {@code IDENTITY} strategy.
 */
@Repository
public class EventRepositoryAdapter implements EventRepository {

  private final SpringDataEventRepository springDataRepo;

  /**
   * Constructs the adapter with the Spring Data repository.
   *
   * @param springDataRepo the Spring Data JPA repository
   */
  public EventRepositoryAdapter(SpringDataEventRepository springDataRepo) {
    this.springDataRepo = springDataRepo;
  }

  /** {@inheritDoc} */
  @Override
  public Event save(Event event) {
    EventJpaEntity entity;
    if (springDataRepo.existsById(event.id().value())) {
      entity = springDataRepo.findById(event.id().value()).orElseThrow();
      EventMapper.updateEntityFields(entity, event);
    } else {
      entity = EventMapper.toJpaEntity(event);
      entity.setId(null); // let JPA auto-generate
    }
    EventJpaEntity saved = springDataRepo.save(entity);
    return EventMapper.toDomain(saved);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Event> findById(EventId id) {
    return springDataRepo.findById(id.value()).map(EventMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public DomainPage<Event> search(EventSearchCriteria criteria, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<EventJpaEntity> result;

    if (criteria.name() != null && criteria.category() != null) {
      result = springDataRepo.findByNameAndCategory(criteria.name(), criteria.category(), pageable);
    } else if (criteria.name() != null) {
      result = springDataRepo.findByName(criteria.name(), pageable);
    } else if (criteria.category() != null) {
      result = springDataRepo.findByCategory(criteria.category(), pageable);
    } else {
      result = springDataRepo.findAll(pageable);
    }

    List<Event> events = result.getContent().stream()
        .map(EventMapper::toDomain)
        .toList();

    PageMeta meta = PageMeta.of(
        result.getNumber(),
        result.getSize(),
        result.getTotalElements(),
        result.getTotalPages());
    return DomainPage.of(events, meta);
  }

  /** {@inheritDoc} */
  @Override
  public List<Event> findByName(String name) {
    return springDataRepo.findByName(name).stream()
        .map(EventMapper::toDomain)
        .toList();
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByName(String name) {
    return springDataRepo.existsByName(name);
  }
}
