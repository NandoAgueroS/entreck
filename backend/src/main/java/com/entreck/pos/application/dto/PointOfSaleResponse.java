package com.entreck.pos.application.dto;

import java.time.Instant;

/**
 * Response DTO for point of sale summary (use cases S01, S02).
 *
 * @param id the point of sale identifier
 * @param name the point of sale name
 * @param status the POS status
 * @param shopId the shop identifier
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record PointOfSaleResponse(
    Long id,
    String name,
    String status,
    Long shopId,
    Instant createdAt,
    Instant updatedAt) {}
