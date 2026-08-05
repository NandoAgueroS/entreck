package com.entreck.event.application.dto;

import com.entreck.shared.domain.enums.EventCategory;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/**
 * Request DTO for updating an existing event (use case O02).
 *
 * <p>All fields are optional — only non-null fields are updated (PATCH
 * semantics per ADR-13).
 *
 * @param name the new event name, or null to leave unchanged
 * @param category the new event category, or null to leave unchanged
 * @param eventDate the new event date, or null to leave unchanged
 * @param description the new event description, or null to leave unchanged
 */
public record UpdateEventRequest(
    @Size(max = 200, message = "Event name must not exceed 200 characters")
    String name,

    EventCategory category,

    Instant eventDate,

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    String description) {
}
