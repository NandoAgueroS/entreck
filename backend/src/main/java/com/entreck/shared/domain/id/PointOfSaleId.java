package com.entreck.shared.domain.id;

/**
 * Typed identifier for a {@link com.entreck.pos.domain.PointOfSale} aggregate.
 *
 * <p>Wrapping the numeric identifier prevents accidental substitution with
 * identifiers from other aggregates.
 *
 * @param value the numeric identifier, must not be null
 */
public record PointOfSaleId(Long value) {

  public PointOfSaleId {
    if (value == null) {
      throw new IllegalArgumentException("Point of sale id is required");
    }
  }
}
