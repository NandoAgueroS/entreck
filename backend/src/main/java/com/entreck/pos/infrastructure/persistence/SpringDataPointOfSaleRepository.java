package com.entreck.pos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link PointOfSaleJpaEntity}.
 *
 * <p>Provides basic CRUD operations for the points_of_sale table. This is an
 * infrastructure concern; the domain port {@code PointOfSaleRepository} is
 * implemented by {@link PointOfSaleRepositoryAdapter}.
 */
public interface SpringDataPointOfSaleRepository extends JpaRepository<PointOfSaleJpaEntity, Long> {
}
