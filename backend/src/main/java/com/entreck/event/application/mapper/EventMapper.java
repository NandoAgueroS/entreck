package com.entreck.event.application.mapper;

import com.entreck.event.application.dto.*;
import com.entreck.event.domain.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventMapper {

  @Mapping(target = "id", expression = "java(event.id().value())")
  @Mapping(target = "name", expression = "java(event.name())")
  @Mapping(target = "category", expression = "java(event.category())")
  @Mapping(target = "eventDate", expression = "java(event.eventDate())")
  @Mapping(target = "status", expression = "java(event.status().name())")
  @Mapping(target = "organizerId", expression = "java(event.organizerId().value())")
  EventSummaryResponse toSummaryResponse(Event event);

  @Mapping(target = "id", expression = "java(event.id().value())")
  @Mapping(target = "name", expression = "java(event.name())")
  @Mapping(target = "category", expression = "java(event.category())")
  @Mapping(target = "eventDate", expression = "java(event.eventDate())")
  @Mapping(target = "description", expression = "java(event.description())")
  @Mapping(target = "status", expression = "java(event.status().name())")
  @Mapping(target = "organizerId", expression = "java(event.organizerId().value())")
  @Mapping(target = "createdAt", expression = "java(event.createdAt())")
  @Mapping(target = "updatedAt", expression = "java(event.updatedAt())")
  EventDetailResponse toDetailResponse(Event event);

  @Mapping(target = "id", expression = "java(event.id().value())")
  @Mapping(target = "name", expression = "java(event.name())")
  @Mapping(target = "category", expression = "java(event.category())")
  @Mapping(target = "eventDate", expression = "java(event.eventDate())")
  @Mapping(target = "description", expression = "java(event.description())")
  @Mapping(target = "status", expression = "java(event.status().name())")
  @Mapping(target = "organizerId", expression = "java(event.organizerId().value())")
  @Mapping(target = "createdAt", expression = "java(event.createdAt())")
  @Mapping(target = "updatedAt", expression = "java(event.updatedAt())")
  EventResponse toResponse(Event event);
}
