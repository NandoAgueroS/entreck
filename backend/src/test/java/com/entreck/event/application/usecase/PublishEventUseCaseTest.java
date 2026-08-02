package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.CreateEventRequest;
import com.entreck.event.application.dto.EventResponse;
import com.entreck.event.application.exception.DuplicateEventNameException;
import com.entreck.event.application.usecase.impl.PublishEventUseCaseImpl;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublishEventUseCaseImpl")
class PublishEventUseCaseTest {

  @Mock
  private EventRepository eventRepository;

  private PublishEventUseCaseImpl useCase;

  private final EventId eventId = new EventId(1L);
  private final OrganizerId organizerId = new OrganizerId(10L);

  @BeforeEach
  void setUp() {
    useCase = new PublishEventUseCaseImpl(eventRepository);
  }

  @Test
  @DisplayName("should create and publish event successfully")
  void shouldCreateAndPublishEventSuccessfully() {
    CreateEventRequest request = new CreateEventRequest(
        "Concert Night",
        EventCategory.CONCERT,
        Instant.now().plusSeconds(86400),
        "A great concert");

    when(eventRepository.existsByName("Concert Night")).thenReturn(false);
    when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

    EventResponse response = useCase.execute(request, organizerId, eventId);

    assertThat(response.id()).isEqualTo(eventId.value());
    assertThat(response.name()).isEqualTo("Concert Night");
    assertThat(response.status()).isEqualTo(EventStatus.PUBLISHED.name());

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
    verify(eventRepository).save(captor.capture());
    assertThat(captor.getValue().status()).isEqualTo(EventStatus.PUBLISHED);
  }

  @Test
  @DisplayName("should throw DuplicateEventNameException when name already exists")
  void shouldThrowDuplicateEventNameException() {
    CreateEventRequest request = new CreateEventRequest(
        "Existing Event",
        EventCategory.CONCERT,
        Instant.now().plusSeconds(86400),
        "Description");

    when(eventRepository.existsByName("Existing Event")).thenReturn(true);

    assertThatThrownBy(() -> useCase.execute(request, organizerId, eventId))
        .isInstanceOf(DuplicateEventNameException.class)
        .hasMessageContaining("Event name already exists");
  }

  @Test
  @DisplayName("should NOT save event when name is duplicate")
  void shouldNotSaveEventWhenNameIsDuplicate() {
    CreateEventRequest request = new CreateEventRequest(
        "Existing Event",
        EventCategory.CONCERT,
        Instant.now().plusSeconds(86400),
        "Description");

    when(eventRepository.existsByName("Existing Event")).thenReturn(true);

    assertThatThrownBy(() -> useCase.execute(request, organizerId, eventId))
        .isInstanceOf(DuplicateEventNameException.class);

    verify(eventRepository, org.mockito.Mockito.never()).save(any());
  }
}
