package com.entreck.shared.domain.enums;

/**
 * Availability signal for tickets at a specific point of sale.
 *
 * <p>This is a data signal, not a real-time inventory count.
 */
public enum AvailabilityStatus {
  AVAILABLE,
  LIMITED,
  SOLD_OUT,
  UNKNOWN
}
