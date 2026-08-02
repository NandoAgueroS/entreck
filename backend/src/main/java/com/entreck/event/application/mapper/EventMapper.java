package com.entreck.event.application.mapper;

import com.entreck.event.application.dto.*;
import com.entreck.event.domain.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

  @Mapping(target = "id", expression = "java(event.id().value())")
  @Mapping(target = "organizerId", expression = "java(event.organizerId().value())")
  EventSummaryResponse toSummaryResponse(Event event);

  @Mapping(target = "id", expression = "java(event.id().value())")
  @Mapping(target = "organizerId", expression = "java(event.organizerId().value())")
  EventDetailResponse toDetailResponse(Event event);

  @Mapping(target = "id", expression = "java(event.id().value())")
  @Mapping(target = "organizerId", expression = "java(event.organizerId().value())")
  EventResponse toResponse(Event event);
}
