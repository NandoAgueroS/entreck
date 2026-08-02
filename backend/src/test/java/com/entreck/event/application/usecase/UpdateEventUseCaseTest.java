package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventResponse;
import com.entreck.event.application.dto.UpdateEventRequest;
import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventStatus;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateEventUseCase")
class UpdateEventUseCaseTest {

  @Mock
  private EventRepository eventRepository;

  private UpdateEventUseCase useCase;

  private final EventId eventId = new EventId(1L);
  private final OrganizerId organizerId = new OrganizerId(10L);
  private final Instant now = Instant.now();

  private Event createEvent(EventStatus status) {
    return new Event(
        eventId,
        "Original Name",
        EventCategory.CONCERT,
        now.plusSeconds(86400),
        "Original description",
        organizerId,
        status,
        now,
        now);
  }

  @BeforeEach
  void setUp() {
    useCase = new UpdateEventUseCase(eventRepository);
  }

  @Test
  @DisplayName("should update event name successfully")
  void shouldUpdateEventNameSuccessfully() {
    Event event = createEvent(EventStatus.DRAFT);
    UpdateEventRequest request = new UpdateEventRequest("New Name", null, null, null);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(eventRepository.existsByName("New Name")).thenReturn(false);
    when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

    EventResponse response = useCase.execute(eventId, request);

    assertThat(response.name()).isEqualTo("New Name");
    verify(eventRepository).save(event);
  }

  @Test
  @DisplayName("should update multiple fields at once")
  void shouldUpdateMultipleFieldsAtOnce() {
    Event event = createEvent(EventStatus.PUBLISHED);
    Instant newDate = now.plusSeconds(172800);
    UpdateEventRequest request = new UpdateEventRequest(
        "Updated Name", EventCategory.SPORTS, newDate, "Updated description");

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(eventRepository.existsByName("Updated Name")).thenReturn(false);
    when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

    EventResponse response = useCase.execute(eventId, request);

    assertThat(response.name()).isEqualTo("Updated Name");
    assertThat(response.category()).isEqualTo(EventCategory.SPORTS);
    assertThat(response.eventDate()).isEqualTo(newDate);
  }

  @Test
  @DisplayName("should keep original values for null fields")
  void shouldKeepOriginalValuesForNullFields() {
    Event event = createEvent(EventStatus.DRAFT);
    UpdateEventRequest request = new UpdateEventRequest(null, null, null, "New desc only");

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

    EventResponse response = useCase.execute(eventId, request);

    assertThat(response.name()).isEqualTo("Original Name");
    assertThat(response.category()).isEqualTo(EventCategory.CONCERT);
    assertThat(response.description()).isEqualTo("New desc only");
  }

  @Test
  @DisplayName("should throw EventNotFoundException when event does not exist")
  void shouldThrowEventNotFoundException() {
    UpdateEventRequest request = new UpdateEventRequest("New", null, null, null);

    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(eventId, request))
        .isInstanceOf(EventNotFoundException.class)
        .hasMessageContaining("Event not found");
  }

  @Test
  @DisplayName("should throw DuplicateEventNameException when new name conflicts")
  void shouldThrowDuplicateEventNameException() {
    Event event = createEvent(EventStatus.DRAFT);
    UpdateEventRequest request = new UpdateEventRequest("Existing Name", null, null, null);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(eventRepository.existsByName("Existing Name")).thenReturn(true);

    assertThatThrownBy(() -> useCase.execute(eventId, request))
        .isInstanceOf(DuplicateEventNameException.class)
        .hasMessageContaining("Event name already exists");
  }

  @Test
  @DisplayName("should throw IllegalStateException when updating CANCELLED event")
  void shouldThrowIllegalStateExceptionWhenUpdatingCancelledEvent() {
    Event event = createEvent(EventStatus.CANCELLED);
    UpdateEventRequest request = new UpdateEventRequest("New", null, null, null);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    assertThatThrownBy(() -> useCase.execute(eventId, request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot update a CANCELLED event");
  }
}
