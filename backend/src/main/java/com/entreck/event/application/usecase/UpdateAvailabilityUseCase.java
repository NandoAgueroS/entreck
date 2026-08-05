package com.entreck.event.application.usecase;

import com.entreck.event.application.dto.AvailabilityRequest;
import com.entreck.event.application.dto.AvailabilityResponse;
import com.entreck.shared.domain.id.EventId;
import com.entreck.shared.domain.id.PointOfSaleId;

/**
 * Port for the update-availability use case (S03).
 *
 * <p>Replaces the availability status and optional note for an existing
 * EventPointOfSaleLink.
 */
public interface UpdateAvailabilityUseCase {

  /**
   * Updates the availability of a link.
   *
   * @param eventId the event identifier
   * @param posId the point-of-sale identifier
   * @param request the availability update request
   * @return the updated availability response
   */
  AvailabilityResponse execute(EventId eventId, PointOfSaleId posId, AvailabilityRequest request);
}
