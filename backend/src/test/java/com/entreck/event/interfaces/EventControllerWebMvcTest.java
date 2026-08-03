package com.entreck.event.interfaces;

import com.entreck.config.SecurityConfig;
import com.entreck.event.application.dto.CreateEventRequest;
import com.entreck.event.application.dto.EventDetailResponse;
import com.entreck.event.application.dto.EventResponse;
import com.entreck.event.application.dto.EventSummaryResponse;
import com.entreck.event.application.dto.UpdateEventRequest;
import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.usecase.FindEventsUseCase;
import com.entreck.event.application.usecase.GetEventDetailUseCase;
import com.entreck.event.application.usecase.PublishEventUseCase;
import com.entreck.event.application.usecase.UpdateEventUseCase;
import com.entreck.shared.domain.DomainPage;
import com.entreck.shared.domain.PageMeta;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvc tests for {@link EventController}.
 *
 * <p>Uses {@link MockMvc} with mocked use cases to verify HTTP mappings,
 * validation, status codes, and JSON response shapes.
 */
@WebMvcTest(EventController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class EventControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PublishEventUseCase publishEventUseCase;

  @MockitoBean
  private UpdateEventUseCase updateEventUseCase;

  @MockitoBean
  private FindEventsUseCase findEventsUseCase;

  @MockitoBean
  private GetEventDetailUseCase getEventDetailUseCase;

  @Test
  void searchEvents_returns200WithPageEnvelope() throws Exception {
    EventSummaryResponse summary = new EventSummaryResponse(
        1L, "Concert", EventCategory.CONCERT,
        Instant.parse("2026-12-01T20:00:00Z"), "PUBLISHED", 10L);
    PageMeta meta = PageMeta.of(0, 20, 1, 1);
    DomainPage<EventSummaryResponse> page = DomainPage.of(List.of(summary), meta);

    when(findEventsUseCase.execute(eq("Concert"), eq(EventCategory.CONCERT), eq(0), eq(20)))
        .thenReturn(page);

    mockMvc.perform(get("/api/v1/events")
            .param("name", "Concert")
            .param("category", "CONCERT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Concert"))
        .andExpect(jsonPath("$.meta.page").value(0))
        .andExpect(jsonPath("$.meta.totalElements").value(1))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true));
  }

  @Test
  void searchEvents_capsPageSizeAt100() throws Exception {
    PageMeta meta = PageMeta.of(0, 100, 0, 0);
    DomainPage<EventSummaryResponse> page = DomainPage.of(List.of(), meta);

    when(findEventsUseCase.execute(any(), any(), eq(0), eq(100)))
        .thenReturn(page);

    mockMvc.perform(get("/api/v1/events").param("size", "1000"))
        .andExpect(status().isOk());
  }

  @Test
  void getEventDetail_returns200() throws Exception {
    EventDetailResponse detail = new EventDetailResponse(
        1L, "Concert", EventCategory.CONCERT,
        Instant.parse("2026-12-01T20:00:00Z"), "A great concert",
        "PUBLISHED", 10L, Instant.now(), Instant.now());

    when(getEventDetailUseCase.execute(any(EventId.class))).thenReturn(detail);

    mockMvc.perform(get("/api/v1/events/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Concert"))
        .andExpect(jsonPath("$.description").value("A great concert"));
  }

  @Test
  void getEventDetail_returns404WhenNotFound() throws Exception {
    when(getEventDetailUseCase.execute(any(EventId.class)))
        .thenThrow(new EventNotFoundException(new EventId(999L)));

    mockMvc.perform(get("/api/v1/events/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("EVENT_NOT_FOUND"));
  }

  @Test
  void publishEvent_returns201() throws Exception {
    EventResponse response = new EventResponse(
        1L, "New Event", EventCategory.CONCERT,
        Instant.parse("2026-12-01T20:00:00Z"), "Description",
        "PUBLISHED", 10L, Instant.now(), Instant.now());

    when(publishEventUseCase.execute(any(), any(), any())).thenReturn(response);

    String body = """
        {
          "name": "New Event",
          "category": "CONCERT",
          "eventDate": "2026-12-01T20:00:00Z",
          "description": "Description"
        }
        """;

    mockMvc.perform(post("/api/v1/events")
            .param("organizerId", "10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("New Event"))
        .andExpect(jsonPath("$.status").value("PUBLISHED"));
  }

  @Test
  void publishEvent_returns400WhenNameBlank() throws Exception {
    String body = """
        {
          "name": "",
          "category": "CONCERT",
          "eventDate": "2026-12-01T20:00:00Z"
        }
        """;

    mockMvc.perform(post("/api/v1/events")
            .param("organizerId", "10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
  }

  @Test
  void publishEvent_returns400WhenCategoryMissing() throws Exception {
    String body = """
        {
          "name": "Valid Name",
          "eventDate": "2026-12-01T20:00:00Z"
        }
        """;

    mockMvc.perform(post("/api/v1/events")
            .param("organizerId", "10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void publishEvent_returns422WhenDuplicateName() throws Exception {
    when(publishEventUseCase.execute(any(), any(), any()))
        .thenThrow(new DuplicateEventNameException("Existing Name"));

    String body = """
        {
          "name": "Existing Name",
          "category": "CONCERT",
          "eventDate": "2026-12-01T20:00:00Z"
        }
        """;

    mockMvc.perform(post("/api/v1/events")
            .param("organizerId", "10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value("DUPLICATE_EVENT_NAME"));
  }

  @Test
  void updateEvent_returns200() throws Exception {
    EventResponse response = new EventResponse(
        1L, "Updated Name", EventCategory.SPORTS,
        Instant.parse("2026-12-01T20:00:00Z"), "Updated description",
        "PUBLISHED", 10L, Instant.now(), Instant.now());

    when(updateEventUseCase.execute(any(EventId.class), any(UpdateEventRequest.class)))
        .thenReturn(response);

    String body = """
        {
          "name": "Updated Name",
          "category": "SPORTS"
        }
        """;

    mockMvc.perform(patch("/api/v1/events/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Name"))
        .andExpect(jsonPath("$.category").value("SPORTS"));
  }

  @Test
  void updateEvent_returns400WhenNameTooLong() throws Exception {
    String longName = "A".repeat(201);
    String body = """
        {
          "name": "%s"
        }
        """.formatted(longName);

    mockMvc.perform(patch("/api/v1/events/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }
}
