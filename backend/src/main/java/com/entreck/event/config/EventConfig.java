package com.entreck.event.config;

import com.entreck.event.application.mapper.EventMapper;
import com.entreck.event.application.usecase.AssociatePosToEventUseCase;
import com.entreck.event.application.usecase.DissociatePosFromEventUseCase;
import com.entreck.event.application.usecase.FindEventsUseCase;
import com.entreck.event.application.usecase.GetEventDetailUseCase;
import com.entreck.event.application.usecase.ListEventPointOfSaleUseCase;
import com.entreck.event.application.usecase.PublishEventUseCase;
import com.entreck.event.application.usecase.UpdateAvailabilityUseCase;
import com.entreck.event.application.usecase.UpdateEventUseCase;
import com.entreck.event.application.usecase.impl.AssociatePosToEventUseCaseImpl;
import com.entreck.event.application.usecase.impl.DissociatePosFromEventUseCaseImpl;
import com.entreck.event.application.usecase.impl.FindEventsUseCaseImpl;
import com.entreck.event.application.usecase.impl.GetEventDetailUseCaseImpl;
import com.entreck.event.application.usecase.impl.ListEventPointOfSaleUseCaseImpl;
import com.entreck.event.application.usecase.impl.PublishEventUseCaseImpl;
import com.entreck.event.application.usecase.impl.UpdateAvailabilityUseCaseImpl;
import com.entreck.event.application.usecase.impl.UpdateEventUseCaseImpl;
import com.entreck.event.domain.repository.EventRepository;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import com.entreck.shared.domain.link.repository.EventPointOfSaleLinkRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration wiring event use cases.
 *
 * <p>Use case implementations are plain Java classes (no Spring annotations).
 * This configuration creates Spring beans for each use case, injecting the
 * domain port and the application-layer MapStruct mapper.
 */
@Configuration
public class EventConfig {

  /**
   * Wires the publish-event use case (O01).
   *
   * @param eventRepository the event repository port
   * @param eventMapper the application-layer DTO mapper
   * @return the use case bean
   */
  @Bean
  public PublishEventUseCase publishEventUseCase(
      EventRepository eventRepository, EventMapper eventMapper) {
    return new PublishEventUseCaseImpl(eventRepository, eventMapper);
  }

  /**
   * Wires the update-event use case (O02).
   *
   * @param eventRepository the event repository port
   * @param eventMapper the application-layer DTO mapper
   * @return the use case bean
   */
  @Bean
  public UpdateEventUseCase updateEventUseCase(
      EventRepository eventRepository, EventMapper eventMapper) {
    return new UpdateEventUseCaseImpl(eventRepository, eventMapper);
  }

  /**
   * Wires the find-events use case (B01, B02).
   *
   * @param eventRepository the event repository port
   * @param eventMapper the application-layer DTO mapper
   * @return the use case bean
   */
  @Bean
  public FindEventsUseCase findEventsUseCase(
      EventRepository eventRepository, EventMapper eventMapper) {
    return new FindEventsUseCaseImpl(eventRepository, eventMapper);
  }

  /**
   * Wires the get-event-detail use case (B03).
   *
   * @param eventRepository the event repository port
   * @param eventMapper the application-layer DTO mapper
   * @return the use case bean
   */
  @Bean
  public GetEventDetailUseCase getEventDetailUseCase(
      EventRepository eventRepository, EventMapper eventMapper) {
    return new GetEventDetailUseCaseImpl(eventRepository, eventMapper);
  }

  /**
   * Wires the associate-POS-to-event use case (O03).
   *
   * @param eventRepository the event repository port
   * @param pointOfSaleRepository the POS repository port
   * @param linkRepository the link repository port
   * @return the use case bean
   */
  @Bean
  public AssociatePosToEventUseCase associatePosToEventUseCase(
      EventRepository eventRepository,
      PointOfSaleRepository pointOfSaleRepository,
      EventPointOfSaleLinkRepository linkRepository) {
    return new AssociatePosToEventUseCaseImpl(eventRepository, pointOfSaleRepository, linkRepository);
  }

  /**
   * Wires the dissociate-POS-from-event use case (O04).
   *
   * @param eventRepository the event repository port
   * @param pointOfSaleRepository the POS repository port
   * @param linkRepository the link repository port
   * @return the use case bean
   */
  @Bean
  public DissociatePosFromEventUseCase dissociatePosFromEventUseCase(
      EventRepository eventRepository,
      PointOfSaleRepository pointOfSaleRepository,
      EventPointOfSaleLinkRepository linkRepository) {
    return new DissociatePosFromEventUseCaseImpl(eventRepository, pointOfSaleRepository, linkRepository);
  }

  /**
   * Wires the list-event-POS use case (B04).
   *
   * @param eventRepository the event repository port
   * @param pointOfSaleRepository the POS repository port
   * @param linkRepository the link repository port
   * @return the use case bean
   */
  @Bean
  public ListEventPointOfSaleUseCase listEventPointOfSaleUseCase(
      EventRepository eventRepository,
      PointOfSaleRepository pointOfSaleRepository,
      EventPointOfSaleLinkRepository linkRepository) {
    return new ListEventPointOfSaleUseCaseImpl(eventRepository, pointOfSaleRepository, linkRepository);
  }

  /**
   * Wires the update-availability use case (S03).
   *
   * @param linkRepository the link repository port
   * @return the use case bean
   */
  @Bean
  public UpdateAvailabilityUseCase updateAvailabilityUseCase(
      EventPointOfSaleLinkRepository linkRepository) {
    return new UpdateAvailabilityUseCaseImpl(linkRepository);
  }
}
