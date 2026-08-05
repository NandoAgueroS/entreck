package com.entreck.pos.infrastructure.persistence;

import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.id.PointOfSaleId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA adapter implementing the domain {@link PointOfSaleRepository} port.
 *
 * <p>Translates between domain types and JPA entities using the static
 * {@link PointOfSaleMapper}. New POS aggregates (where the domain ID does
 * not yet exist in the database) are persisted with a null JPA ID so that
 * PostgreSQL generates the identifier via the {@code IDENTITY} strategy.
 */
@Repository
public class PointOfSaleRepositoryAdapter implements PointOfSaleRepository {

  private final SpringDataPointOfSaleRepository springDataRepo;

  /**
   * Constructs the adapter with the Spring Data repository.
   *
   * @param springDataRepo the Spring Data JPA repository
   */
  public PointOfSaleRepositoryAdapter(SpringDataPointOfSaleRepository springDataRepo) {
    this.springDataRepo = springDataRepo;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public PointOfSale save(PointOfSale pointOfSale) {
    PointOfSaleJpaEntity entity;
    if (pointOfSale.id().value() != null) {
      entity = springDataRepo.findById(pointOfSale.id().value()).orElse(null);
      if (entity != null) {
        PointOfSaleMapper.updateEntityFields(entity, pointOfSale);
      } else {
        entity = PointOfSaleMapper.toJpaEntity(pointOfSale);
        entity.setId(null); // let JPA auto-generate
      }
    } else {
      entity = PointOfSaleMapper.toJpaEntity(pointOfSale);
      entity.setId(null); // let JPA auto-generate
    }
    PointOfSaleJpaEntity saved = springDataRepo.save(entity);
    return PointOfSaleMapper.toDomain(saved);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<PointOfSale> findById(PointOfSaleId id) {
    return springDataRepo.findById(id.value()).map(PointOfSaleMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsById(PointOfSaleId id) {
    return springDataRepo.existsById(id.value());
  }
}
