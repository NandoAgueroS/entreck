package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.AvailabilityRequest;
import com.entreck.event.application.dto.AvailabilityResponse;
import com.entreck.event.application.exception.LinkNotFoundException;
import com.entreck.event.application.usecase.impl.UpdateAvailabilityUseCaseImpl;
import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.link.LinkId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.link.EventPointOfSaleLink;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateAvailabilityUseCaseImpl")
class UpdateAvailabilityUseCaseTest {

  @Mock
  private EventPointOfSaleLinkRepository linkRepository;

  private UpdateAvailabilityUseCaseImpl useCase;

  private final EventId eventId = new EventId(1L);
  private final PointOfSaleId posId = new PointOfSaleId(2L);

  @BeforeEach
  void setUp() {
    useCase = new UpdateAvailabilityUseCaseImpl(linkRepository);
  }

  @Test
  @DisplayName("should update availability successfully")
  void shouldUpdateAvailabilitySuccessfully() {
    Instant now = Instant.now();
    EventPointOfSaleLink existingLink = new EventPointOfSaleLink(
        new LinkId(10L), eventId, posId, AvailabilityStatus.UNKNOWN, null, now);

    when(linkRepository.findByEventIdAndPosId(eventId, posId))
        .thenReturn(Optional.of(existingLink));

    lenient().when(linkRepository.save(any(EventPointOfSaleLink.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AvailabilityRequest request = new AvailabilityRequest(AvailabilityStatus.LIMITED, "Low stock");
    AvailabilityResponse response = useCase.execute(eventId, posId, request);

    assertThat(response.linkId()).isEqualTo(10L);
    assertThat(response.availabilityStatus()).isEqualTo(AvailabilityStatus.LIMITED);
    assertThat(response.note()).isEqualTo("Low stock");
  }

  @Test
  @DisplayName("should throw LinkNotFoundException when link does not exist")
  void shouldThrowLinkNotFoundException() {
    when(linkRepository.findByEventIdAndPosId(eventId, posId))
        .thenReturn(Optional.empty());

    AvailabilityRequest request = new AvailabilityRequest(AvailabilityStatus.SOLD_OUT, null);
    assertThatThrownBy(() -> useCase.execute(eventId, posId, request))
        .isInstanceOf(LinkNotFoundException.class);
  }
}
