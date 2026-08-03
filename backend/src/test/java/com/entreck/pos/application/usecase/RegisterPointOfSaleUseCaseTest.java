package com.entreck.pos.application.usecase;

import com.entreck.pos.application.dto.*;
import com.entreck.pos.application.mapper.PointOfSaleMapper;
import com.entreck.pos.application.usecase.impl.RegisterPointOfSaleUseCaseImpl;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterPointOfSaleUseCaseImpl")
class RegisterPointOfSaleUseCaseTest {

  @Mock
  private PointOfSaleRepository pointOfSaleRepository;

  @Mock
  private PointOfSaleMapper pointOfSaleMapper;

  private RegisterPointOfSaleUseCaseImpl useCase;

  private final PointOfSaleId posId = new PointOfSaleId(1L);
  private final ShopId shopId = new ShopId(10L);

  @BeforeEach
  void setUp() {
    useCase = new RegisterPointOfSaleUseCaseImpl(pointOfSaleRepository, pointOfSaleMapper);
    lenient().when(pointOfSaleMapper.toResponse(any(PointOfSale.class))).thenAnswer(invocation -> {
      PointOfSale pos = invocation.getArgument(0);
      return new PointOfSaleResponse(
          pos.id().value(), pos.name(), pos.status().name(),
          pos.shopId().value(), pos.createdAt(), pos.updatedAt());
    });
    lenient().when(pointOfSaleMapper.toAddress(any(AddressDto.class))).thenAnswer(invocation -> {
      AddressDto dto = invocation.getArgument(0);
      return new Address(dto.street(), dto.city(), dto.state(), dto.zipCode(), dto.country());
    });
    lenient().when(pointOfSaleMapper.toGeoLocation(any(GeoLocationDto.class))).thenAnswer(invocation -> {
      GeoLocationDto dto = invocation.getArgument(0);
      return new GeoLocation(dto.latitude(), dto.longitude());
    });
    lenient().when(pointOfSaleMapper.toContact(any(ContactDto.class))).thenAnswer(invocation -> {
      ContactDto dto = invocation.getArgument(0);
      return new Contact(dto.email(), dto.phone());
    });
    lenient().when(pointOfSaleMapper.toOpeningHours(any(OpeningHoursDto.class))).thenAnswer(invocation -> {
      OpeningHoursDto dto = invocation.getArgument(0);
      return new OpeningHours(
          dto.windows().stream()
              .map(w -> new WeeklyWindow(w.dayOfWeek(), w.open(), w.close()))
              .toList(),
          dto.timezone());
    });
  }

  @Test
  @DisplayName("should register point of sale successfully")
  void shouldRegisterPointOfSaleSuccessfully() {
    CreatePointOfSaleRequest request = new CreatePointOfSaleRequest(
        "Ticket Shop",
        new AddressDto("123 Main St", "Springfield", "IL", "62701", "US"),
        new GeoLocationDto(40.7128, -74.0060),
        new ContactDto("shop@example.com", "+1-555-123-4567"),
        new OpeningHoursDto(
            java.util.List.of(new OpeningHoursDto.WeeklyWindowDto(
                java.time.DayOfWeek.MONDAY,
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(17, 0))),
            "America/New_York"),
        shopId.value());

    when(pointOfSaleRepository.save(any(PointOfSale.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PointOfSaleResponse response = useCase.execute(request, posId, shopId);

    assertThat(response.id()).isEqualTo(posId.value());
    assertThat(response.name()).isEqualTo("Ticket Shop");
    assertThat(response.status()).isEqualTo(POSStatus.ACTIVE.name());

    ArgumentCaptor<PointOfSale> captor = ArgumentCaptor.forClass(PointOfSale.class);
    verify(pointOfSaleRepository).save(captor.capture());
    assertThat(captor.getValue().status()).isEqualTo(POSStatus.ACTIVE);
  }
}
