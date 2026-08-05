package com.entreck.shared.domain.id;

/**
 * Typed identifier for a shop that acts as a physical point of sale.
 *
 * <p>In v1 the identifier is accepted from the request body because the
 * authentication system is deferred to v1.1.
 *
 * @param value the numeric identifier, must not be null
 */
public record ShopId(Long value) {

  public ShopId {
    if (value == null) {
      throw new IllegalArgumentException("Shop id is required");
    }
  }
}
