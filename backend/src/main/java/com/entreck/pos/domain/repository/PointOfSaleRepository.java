package com.entreck.pos.domain.repository;

import com.entreck.pos.domain.PointOfSale;
import com.entreck.shared.domain.id.PointOfSaleId;
import java.util.Optional;

/**
 * Port interface for PointOfSale aggregate persistence.
 *
 * <p>Domain-layer contract that infrastructure adapters must implement. The
 * domain layer has no dependency on Spring Data or JPA; this interface uses
 * only domain types.
 */
public interface PointOfSaleRepository {

  /**
   * Saves a PointOfSale aggregate.
   *
   * @param pointOfSale the point of sale to save
   * @return the saved point of sale (with any generated fields populated)
   */
  PointOfSale save(PointOfSale pointOfSale);

  /**
   * Finds a PointOfSale by its identifier.
   *
   * @param id the point of sale identifier
   * @return an Optional containing the point of sale if found, or empty otherwise
   */
  Optional<PointOfSale> findById(PointOfSaleId id);

  /**
   * Checks if a point of sale with the given identifier exists.
   *
   * @param id the point of sale identifier
   * @return true if a point of sale with that id exists, false otherwise
   */
  boolean existsById(PointOfSaleId id);
}
