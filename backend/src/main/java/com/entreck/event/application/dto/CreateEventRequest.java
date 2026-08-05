package com.entreck.event.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.entreck.shared.domain.enums.EventCategory;
import java.time.Instant;

/**
 * Request DTO for creating a new event (use case O01).
 *
 * @param name the event name
 * @param category the event category
 * @param eventDate the date and time of the event
 * @param description the event description
 */
public record CreateEventRequest(
    @NotBlank(message = "Event name is required")
    @Size(max = 200, message = "Event name must not exceed 200 characters")
    String name,

    @NotNull(message = "Event category is required")
    EventCategory category,

    @NotNull(message = "Event date is required")
    Instant eventDate,

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    String description) {
}
