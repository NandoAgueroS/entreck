package com.entreck.pos.application.usecase;

import com.entreck.pos.application.dto.*;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.pos.application.mapper.PointOfSaleMapper;
import com.entreck.pos.application.usecase.impl.UpdatePointOfSaleUseCaseImpl;
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
@DisplayName("UpdatePointOfSaleUseCaseImpl")
class UpdatePointOfSaleUseCaseTest {

  @Mock
  private PointOfSaleRepository pointOfSaleRepository;

  @Mock
  private PointOfSaleMapper pointOfSaleMapper;

  private UpdatePointOfSaleUseCaseImpl useCase;

  private final PointOfSaleId posId = new PointOfSaleId(1L);
  private final ShopId shopId = new ShopId(10L);
  private final Instant now = Instant.now();

  private PointOfSale createPos() {
    Address address = new Address("123 Main St", "Springfield", "IL", "62701", "US");
    GeoLocation geo = new GeoLocation(40.7128, -74.0060);
    Contact contact = new Contact("shop@example.com", "+1-555-123-4567");
    OpeningHours hours = new OpeningHours(
        List.of(new WeeklyWindow(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0))),
        "America/New_York");
    return new PointOfSale(
        posId, "Ticket Shop", address, geo, contact, hours, shopId, POSStatus.ACTIVE, now, now);
  }

  @BeforeEach
  void setUp() {
    useCase = new UpdatePointOfSaleUseCaseImpl(pointOfSaleRepository, pointOfSaleMapper);
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
  @DisplayName("should update POS name successfully")
  void shouldUpdatePosNameSuccessfully() {
    PointOfSale pos = createPos();
    UpdatePointOfSaleRequest request = new UpdatePointOfSaleRequest(
        "New Shop Name", null, null, null, null);

    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.of(pos));
    when(pointOfSaleRepository.save(any(PointOfSale.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    PointOfSaleResponse response = useCase.execute(posId, request);

    assertThat(response.name()).isEqualTo("New Shop Name");
    verify(pointOfSaleRepository).save(pos);
  }

  @Test
  @DisplayName("should keep original values for null fields")
  void shouldKeepOriginalValuesForNullFields() {
    PointOfSale pos = createPos();
    UpdatePointOfSaleRequest request = new UpdatePointOfSaleRequest(
        null, null, null, null, null);

    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.of(pos));
    when(pointOfSaleRepository.save(any(PointOfSale.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    PointOfSaleResponse response = useCase.execute(posId, request);

    assertThat(response.name()).isEqualTo("Ticket Shop");
  }

  @Test
  @DisplayName("should throw PointOfSaleNotFoundException when POS does not exist")
  void shouldThrowPointOfSaleNotFoundException() {
    UpdatePointOfSaleRequest request = new UpdatePointOfSaleRequest("New", null, null, null, null);

    when(pointOfSaleRepository.findById(posId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(posId, request))
        .isInstanceOf(PointOfSaleNotFoundException.class)
        .hasMessageContaining("Point of sale not found");
  }
}
