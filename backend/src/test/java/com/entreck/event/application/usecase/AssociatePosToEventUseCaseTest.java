package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.AssociateRequest;
import com.entreck.event.application.dto.LinkedPosResponse;
import com.entreck.event.application.exception.EventNotFoundException;
import com.entreck.event.application.exception.LinkAlreadyExistsException;
import com.entreck.event.application.usecase.impl.AssociatePosToEventUseCaseImpl;
import com.entreck.event.domain.Event;
import com.entreck.event.domain.EventStatus;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.POSStatus;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.enums.EventCategory;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.OrganizerId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.id.ShopId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
import com.entreck.shared.domain.link.LinkId;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssociatePosToEventUseCaseImpl")
class AssociatePosToEventUseCaseTest {

  @Mock
  private EventRepository eventRepository;

  @Mock
  private PointOfSaleRepository pointOfSaleRepository;

  @Mock
  private EventPointOfSaleLinkRepository linkRepository;

  private AssociatePosToEventUseCaseImpl useCase;

  private final EventId eventId = new EventId(1L);
  private final PointOfSaleId posId = new PointOfSaleId(2L);

  @BeforeEach
  void setUp() {
    useCase = new AssociatePosToEventUseCaseImpl(eventRepository, pointOfSaleRepository, linkRepository);
  }

  private Event createEvent() {
    Instant now = Instant.now();
    return new Event(eventId, "Concert", EventCategory.CONCERT,
        now.plusSeconds(86400), "Description", new OrganizerId(10L),
        EventStatus.PUBLISHED, now, now);
  }

  private PointOfSale createPos() {
    Instant now = Instant.now();
    return new PointOfSale(posId, "Ticket Shop",
        new com.entreck.pos.domain.valueobject.Address("123 Main St", "Buenos Aires", "BA", "C1000", "AR"),
        new com.entreck.pos.domain.valueobject.GeoLocation(-34.6037, -58.3816),
        new com.entreck.pos.domain.valueobject.Contact("shop@test.com", "+54 11 1234-5678"),
        new com.entreck.pos.domain.valueobject.OpeningHours(
            java.util.List.of(new com.entreck.pos.domain.valueobject.WeeklyWindow(
                java.time.DayOfWeek.MONDAY, java.time.LocalTime.of(9, 0), java.time.LocalTime.of(18, 0))),
            "America/Argentina/Buenos_Aires"),
        new ShopId(20L), POSStatus.ACTIVE, now, now);
  }

  @Test
  @DisplayName("should associate POS to event successfully")
  void shouldAssociatePosToEventSuccessfully() {
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(createEvent()));
    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.of(createPos()));
    when(linkRepository.existsByEventIdAndPosId(eventId, posId)).thenReturn(false);

    lenient().when(linkRepository.save(any(EventPointOfSaleLink.class)))
        .thenAnswer(invocation -> {
          EventPointOfSaleLink link = invocation.getArgument(0);
          return new EventPointOfSaleLink(
              new LinkId(1L), link.eventId(), link.posId(),
              link.availabilityStatus(), link.note(), link.updatedAt());
        });

    AssociateRequest request = new AssociateRequest(2L, "First batch");
    LinkedPosResponse response = useCase.execute(eventId, request);

    assertThat(response.linkId()).isEqualTo(1L);
    assertThat(response.eventId()).isEqualTo(1L);
    assertThat(response.posId()).isEqualTo(2L);
    assertThat(response.availabilityStatus()).isEqualTo(AvailabilityStatus.UNKNOWN);
    assertThat(response.note()).isEqualTo("First batch");
  }

  @Test
  @DisplayName("should throw EventNotFoundException when event does not exist")
  void shouldThrowEventNotFoundException() {
    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    AssociateRequest request = new AssociateRequest(2L, null);
    assertThatThrownBy(() -> useCase.execute(eventId, request))
        .isInstanceOf(EventNotFoundException.class);

    verify(linkRepository, never()).save(any());
  }

  @Test
  @DisplayName("should throw LinkAlreadyExistsException for duplicate link")
  void shouldThrowLinkAlreadyExistsException() {
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(createEvent()));
    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.of(createPos()));
    when(linkRepository.existsByEventIdAndPosId(eventId, posId)).thenReturn(true);

    AssociateRequest request = new AssociateRequest(2L, null);
    assertThatThrownBy(() -> useCase.execute(eventId, request))
        .isInstanceOf(LinkAlreadyExistsException.class);

    verify(linkRepository, never()).save(any());
  }

  @Test
  @DisplayName("should throw PointOfSaleNotFoundException when POS does not exist")
  void shouldThrowPointOfSaleNotFoundException() {
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(createEvent()));
    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.empty());

    AssociateRequest request = new AssociateRequest(2L, null);
    assertThatThrownBy(() -> useCase.execute(eventId, request))
        .isInstanceOf(com.entreck.pos.application.exception.PointOfSaleNotFoundException.class);

    verify(linkRepository, never()).save(any());
  }
}
