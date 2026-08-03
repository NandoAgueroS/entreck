package com.entreck.pos.application.usecase;

import com.entreck.pos.application.dto.PointOfSaleDetailResponse;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.pos.application.mapper.PointOfSaleMapper;
import com.entreck.pos.application.usecase.impl.GetPointOfSaleDetailUseCaseImpl;
import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.POSStatus;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.pos.domain.valueobject.*;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.domain.id.ShopId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPointOfSaleDetailUseCaseImpl")
class GetPointOfSaleDetailUseCaseTest {

  @Mock
  private PointOfSaleRepository pointOfSaleRepository;

  @Mock
  private PointOfSaleMapper pointOfSaleMapper;

  private GetPointOfSaleDetailUseCaseImpl useCase;

  private final PointOfSaleId posId = new PointOfSaleId(1L);
  private final ShopId shopId = new ShopId(10L);
  private final Instant now = Instant.now();

  @BeforeEach
  void setUp() {
    useCase = new GetPointOfSaleDetailUseCaseImpl(pointOfSaleRepository, pointOfSaleMapper);
    lenient().when(pointOfSaleMapper.toDetailResponse(any(PointOfSale.class))).thenAnswer(invocation -> {
      PointOfSale pos = invocation.getArgument(0);
      return new PointOfSaleDetailResponse(
          pos.id().value(), pos.name(), null, null, null, null,
          pos.status().name(), pos.shopId().value(), pos.createdAt(), pos.updatedAt());
    });
  }

  @Test
  @DisplayName("should return POS detail when POS exists")
  void shouldReturnPosDetailWhenPosExists() {
    Address address = new Address("123 Main St", "Springfield", "IL", "62701", "US");
    GeoLocation geo = new GeoLocation(40.7128, -74.0060);
    Contact contact = new Contact("shop@example.com", "+1-555-123-4567");
    OpeningHours hours = new OpeningHours(
        List.of(new WeeklyWindow(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0))),
        "America/New_York");
    PointOfSale pos = new PointOfSale(
        posId, "Ticket Shop", address, geo, contact, hours,
        shopId, POSStatus.ACTIVE, now, now);

    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.of(pos));

    PointOfSaleDetailResponse response = useCase.execute(posId);

    assertThat(response.id()).isEqualTo(posId.value());
    assertThat(response.name()).isEqualTo("Ticket Shop");
    assertThat(response.status()).isEqualTo(POSStatus.ACTIVE.name());
    assertThat(response.shopId()).isEqualTo(shopId.value());
    verify(pointOfSaleRepository).findById(posId);
  }

  @Test
  @DisplayName("should throw PointOfSaleNotFoundException when POS does not exist")
  void shouldThrowPointOfSaleNotFoundException() {
    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(posId))
        .isInstanceOf(PointOfSaleNotFoundException.class)
        .hasMessageContaining("Point of sale not found");
  }
}
