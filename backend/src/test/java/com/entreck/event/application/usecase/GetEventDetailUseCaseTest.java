package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.EventDetailResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetEventDetailUseCase")
class GetEventDetailUseCaseTest {

  @Mock
  private EventRepository eventRepository;

  private GetEventDetailUseCase useCase;

  private final EventId eventId = new EventId(1L);
  private final OrganizerId organizerId = new OrganizerId(10L);
  private final Instant now = Instant.now();

  @BeforeEach
  void setUp() {
    useCase = new GetEventDetailUseCase(eventRepository);
  }

  @Test
  @DisplayName("should return event detail when event exists")
  void shouldReturnEventDetailWhenEventExists() {
    Event event = new Event(
        eventId,
        "Concert Night",
        EventCategory.CONCERT,
        now.plusSeconds(86400),
        "A great concert",
        organizerId,
        EventStatus.PUBLISHED,
        now,
        now);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    EventDetailResponse response = useCase.execute(eventId);

    assertThat(response.id()).isEqualTo(eventId.value());
    assertThat(response.name()).isEqualTo("Concert Night");
    assertThat(response.category()).isEqualTo(EventCategory.CONCERT);
    assertThat(response.description()).isEqualTo("A great concert");
    assertThat(response.status()).isEqualTo(EventStatus.PUBLISHED.name());
    assertThat(response.organizerId()).isEqualTo(organizerId.value());
    verify(eventRepository).findById(eventId);
  }

  @Test
  @DisplayName("should throw EventNotFoundException when event does not exist")
  void shouldThrowEventNotFoundExceptionWhenEventDoesNotExist() {
    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(eventId))
        .isInstanceOf(EventNotFoundException.class)
        .hasMessageContaining("Event not found");
  }

  @Test
  @DisplayName("should return event detail for DRAFT event")
  void shouldReturnEventDetailForDraftEvent() {
    Event event = new Event(
        eventId,
        "Draft Event",
        EventCategory.THEATER,
        now.plusSeconds(86400),
        "Draft description",
        organizerId,
        EventStatus.DRAFT,
        now,
        now);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    EventDetailResponse response = useCase.execute(eventId);

    assertThat(response.status()).isEqualTo(EventStatus.DRAFT.name());
  }

  @Test
  @DisplayName("should return event detail for CANCELLED event")
  void shouldReturnEventDetailForCancelledEvent() {
    Event event = new Event(
        eventId,
        "Cancelled Event",
        EventCategory.SPORTS,
        now.plusSeconds(86400),
        "Cancelled description",
        organizerId,
        EventStatus.CANCELLED,
        now,
        now);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    EventDetailResponse response = useCase.execute(eventId);

    assertThat(response.status()).isEqualTo(EventStatus.CANCELLED.name());
  }
}
