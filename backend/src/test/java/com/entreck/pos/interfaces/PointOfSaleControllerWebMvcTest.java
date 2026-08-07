package com.entreck.pos.interfaces;

import com.entreck.config.SecurityConfig;
import com.entreck.event.application.dto.AvailabilityRequest;
import com.entreck.event.application.dto.AvailabilityResponse;
import com.entreck.event.application.exception.LinkNotFoundException;
import com.entreck.event.application.usecase.UpdateAvailabilityUseCase;
import com.entreck.pos.application.dto.AddressDto;
import com.entreck.pos.application.dto.ContactDto;
import com.entreck.pos.application.dto.CreatePointOfSaleRequest;
import com.entreck.pos.application.dto.GeoLocationDto;
import com.entreck.pos.application.dto.OpeningHoursDto;
import com.entreck.pos.application.dto.PointOfSaleDetailResponse;
import com.entreck.pos.application.dto.PointOfSaleResponse;
import com.entreck.pos.application.dto.UpdatePointOfSaleRequest;
import com.entreck.pos.application.exception.PointOfSaleNotFoundException;
import com.entreck.pos.application.usecase.GetPointOfSaleDetailUseCase;
import com.entreck.pos.application.usecase.RegisterPointOfSaleUseCase;
import com.entreck.pos.application.usecase.UpdatePointOfSaleUseCase;
import com.entreck.shared.domain.enums.AvailabilityStatus;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;
import com.entreck.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvc tests for {@link PointOfSaleController}.
 *
 * <p>Uses {@link MockMvc} with mocked use cases to verify HTTP mappings,
 * validation (including nested lat/lon), status codes, and JSON response shapes.
 */
@WebMvcTest(PointOfSaleController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class PointOfSaleControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RegisterPointOfSaleUseCase registerPointOfSaleUseCase;

  @MockitoBean
  private UpdatePointOfSaleUseCase updatePointOfSaleUseCase;

  @MockitoBean
  private GetPointOfSaleDetailUseCase getPointOfSaleDetailUseCase;

  @MockitoBean
  private UpdateAvailabilityUseCase updateAvailabilityUseCase;

  @Test
  void getPointOfSaleDetail_returns200() throws Exception {
    PointOfSaleDetailResponse detail = new PointOfSaleDetailResponse(
        1L, "Ticket Shop",
        new AddressDto("123 Main St", "Buenos Aires", "BA", "C1000", "AR"),
        new GeoLocationDto(-34.6037, -58.3816),
        new ContactDto("shop@test.com", "+54 11 1234-5678"),
        new OpeningHoursDto(
            List.of(new OpeningHoursDto.WeeklyWindowDto(
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0))),
            "America/Argentina/Buenos_Aires"),
        "ACTIVE", 10L, Instant.now(), Instant.now());

    when(getPointOfSaleDetailUseCase.execute(any(PointOfSaleId.class))).thenReturn(detail);

    mockMvc.perform(get("/api/v1/points-of-sale/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Ticket Shop"))
        .andExpect(jsonPath("$.address.street").value("123 Main St"))
        .andExpect(jsonPath("$.location.latitude").value(-34.6037))
        .andExpect(jsonPath("$.location.longitude").value(-58.3816))
        .andExpect(jsonPath("$.contact.email").value("shop@test.com"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void getPointOfSaleDetail_returns404WhenNotFound() throws Exception {
    when(getPointOfSaleDetailUseCase.execute(any(PointOfSaleId.class)))
        .thenThrow(new PointOfSaleNotFoundException(new PointOfSaleId(999L)));

    mockMvc.perform(get("/api/v1/points-of-sale/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("POS_NOT_FOUND"));
  }

  @Test
  void registerPointOfSale_returns201() throws Exception {
    PointOfSaleResponse response = new PointOfSaleResponse(
        1L, "New Shop", "ACTIVE", 10L, Instant.now(), Instant.now());

    when(registerPointOfSaleUseCase.execute(any(), any(), any())).thenReturn(response);

    String body = """
        {
          "name": "New Shop",
          "address": {
            "street": "123 Main St",
            "city": "Buenos Aires",
            "state": "BA",
            "zipCode": "C1000",
            "country": "AR"
          },
          "location": {
            "latitude": -34.6037,
            "longitude": -58.3816
          },
          "contact": {
            "email": "shop@test.com",
            "phone": "+54 11 1234-5678"
          },
          "openingHours": {
            "windows": [
              {
                "dayOfWeek": "MONDAY",
                "open": "09:00:00",
                "close": "18:00:00"
              }
            ],
            "timezone": "America/Argentina/Buenos_Aires"
          },
          "shopId": 10
        }
        """;

    mockMvc.perform(post("/api/v1/points-of-sale")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("New Shop"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void registerPointOfSale_returns400WhenNameBlank() throws Exception {
    String body = """
        {
          "name": "",
          "address": {
            "street": "123 Main St",
            "city": "Buenos Aires",
            "state": "BA",
            "zipCode": "C1000",
            "country": "AR"
          },
          "location": {
            "latitude": -34.6037,
            "longitude": -58.3816
          },
          "contact": {
            "email": "shop@test.com",
            "phone": "+54 11 1234-5678"
          },
          "openingHours": {
            "windows": [
              {
                "dayOfWeek": "MONDAY",
                "open": "09:00:00",
                "close": "18:00:00"
              }
            ],
            "timezone": "America/Argentina/Buenos_Aires"
          },
          "shopId": 10
        }
        """;

    mockMvc.perform(post("/api/v1/points-of-sale")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
  }

  @Test
  void registerPointOfSale_returns400WhenLatitudeOutOfBounds() throws Exception {
    String body = """
        {
          "name": "Bad Location Shop",
          "address": {
            "street": "123 Main St",
            "city": "Buenos Aires",
            "state": "BA",
            "zipCode": "C1000",
            "country": "AR"
          },
          "location": {
            "latitude": 100.0,
            "longitude": -58.3816
          },
          "contact": {
            "email": "shop@test.com",
            "phone": "+54 11 1234-5678"
          },
          "openingHours": {
            "windows": [
              {
                "dayOfWeek": "MONDAY",
                "open": "09:00:00",
                "close": "18:00:00"
              }
            ],
            "timezone": "America/Argentina/Buenos_Aires"
          },
          "shopId": 10
        }
        """;

    mockMvc.perform(post("/api/v1/points-of-sale")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("location.latitude"));
  }

  @Test
  void registerPointOfSale_returns400WhenLongitudeOutOfBounds() throws Exception {
    String body = """
        {
          "name": "Bad Location Shop",
          "address": {
            "street": "123 Main St",
            "city": "Buenos Aires",
            "state": "BA",
            "zipCode": "C1000",
            "country": "AR"
          },
          "location": {
            "latitude": -34.6037,
            "longitude": 200.0
          },
          "contact": {
            "email": "shop@test.com",
            "phone": "+54 11 1234-5678"
          },
          "openingHours": {
            "windows": [
              {
                "dayOfWeek": "MONDAY",
                "open": "09:00:00",
                "close": "18:00:00"
              }
            ],
            "timezone": "America/Argentina/Buenos_Aires"
          },
          "shopId": 10
        }
        """;

    mockMvc.perform(post("/api/v1/points-of-sale")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("location.longitude"));
  }

  @Test
  void registerPointOfSale_returns400WhenLocationMissing() throws Exception {
    String body = """
        {
          "name": "No Location Shop",
          "address": {
            "street": "123 Main St",
            "city": "Buenos Aires",
            "state": "BA",
            "zipCode": "C1000",
            "country": "AR"
          },
          "contact": {
            "email": "shop@test.com",
            "phone": "+54 11 1234-5678"
          },
          "openingHours": {
            "windows": [
              {
                "dayOfWeek": "MONDAY",
                "open": "09:00:00",
                "close": "18:00:00"
              }
            ],
            "timezone": "America/Argentina/Buenos_Aires"
          },
          "shopId": 10
        }
        """;

    mockMvc.perform(post("/api/v1/points-of-sale")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void updatePointOfSale_returns200() throws Exception {
    PointOfSaleResponse response = new PointOfSaleResponse(
        1L, "Updated Shop", "ACTIVE", 10L, Instant.now(), Instant.now());

    when(updatePointOfSaleUseCase.execute(any(PointOfSaleId.class), any(UpdatePointOfSaleRequest.class)))
        .thenReturn(response);

    String body = """
        {
          "name": "Updated Shop"
        }
        """;

    mockMvc.perform(patch("/api/v1/points-of-sale/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Shop"));
  }

  @Test
  void updatePointOfSale_returns400WhenNameTooLong() throws Exception {
    String longName = "A".repeat(201);
    String body = """
        {
          "name": "%s"
        }
        """.formatted(longName);

    mockMvc.perform(patch("/api/v1/points-of-sale/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void updatePointOfSale_returns400WhenLatitudeInvalid() throws Exception {
    String body = """
        {
          "location": {
            "latitude": -100.0,
            "longitude": 0.0
          }
        }
        """;

    mockMvc.perform(patch("/api/v1/points-of-sale/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("location.latitude"));
  }

  // --- S03: Update availability ---

  @Test
  void updateAvailability_returns200() throws Exception {
    AvailabilityResponse response = new AvailabilityResponse(
        1L, 10L, 1L, AvailabilityStatus.LIMITED, "Low stock", Instant.now());
    when(updateAvailabilityUseCase.execute(
        any(EventId.class), any(PointOfSaleId.class), any(AvailabilityRequest.class)))
        .thenReturn(response);

    String body = """
        {
          "availabilityStatus": "LIMITED",
          "note": "Low stock"
        }
        """;

    mockMvc.perform(put("/api/v1/points-of-sale/1/events/10/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.linkId").value(1))
        .andExpect(jsonPath("$.availabilityStatus").value("LIMITED"))
        .andExpect(jsonPath("$.note").value("Low stock"));
  }

  @Test
  void updateAvailability_returns400WhenStatusNull() throws Exception {
    String body = """
        {
          "note": "Missing status"
        }
        """;

    mockMvc.perform(put("/api/v1/points-of-sale/1/events/10/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void updateAvailability_returns400WhenNoteTooLong() throws Exception {
    String body = """
        {
          "availabilityStatus": "LIMITED",
          "note": "%s"
        }
        """.formatted("A".repeat(141));

    mockMvc.perform(put("/api/v1/points-of-sale/1/events/10/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("note"));
  }

  @Test
  void updateAvailability_returns404WhenLinkNotFound() throws Exception {
    when(updateAvailabilityUseCase.execute(
        any(EventId.class), any(PointOfSaleId.class), any(AvailabilityRequest.class)))
        .thenThrow(new LinkNotFoundException(new EventId(10L), new PointOfSaleId(1L)));

    String body = """
        {
          "availabilityStatus": "SOLD_OUT"
        }
        """;

    mockMvc.perform(put("/api/v1/points-of-sale/1/events/10/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("LINK_NOT_FOUND"));
  }
}
