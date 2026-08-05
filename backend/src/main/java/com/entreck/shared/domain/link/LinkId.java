package com.entreck.shared.domain.link;

import java.util.Objects;

/**
 * Typed identifier for an EventPointOfSaleLink aggregate.
 *
 * <p>Wraps a non-null {@link Long} value following ADR-5 (typed ID records).
 * Prevents accidental mixing of link IDs with other entity identifiers.
 *
 * @param value the link identifier
 */
public record LinkId(Long value) {

  /**
   * Constructs a LinkId with null-check validation.
   *
   * @param value the link identifier
   * @throws NullPointerException if value is null
   */
  public LinkId {
    Objects.requireNonNull(value, "LinkId value must not be null");
  }
}
