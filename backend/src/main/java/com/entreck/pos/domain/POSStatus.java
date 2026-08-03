package com.entreck.pos.domain;

/**
 * Operational status of a {@link PointOfSale} aggregate.
 *
 * <p>A point of sale can be active (operating normally) or inactive
 * (temporarily or permanently closed).
 */
public enum POSStatus {
  /**
   * Point of sale is operating normally.
   */
  ACTIVE,

  /**
   * Point of sale is temporarily or permanently closed.
   */
  INACTIVE
}
