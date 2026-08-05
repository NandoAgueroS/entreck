package com.entreck.pos.application.mapper;

import com.entreck.pos.application.dto.*;
import com.entreck.pos.domain.PointOfSale;
import com.entreck.pos.domain.valueobject.Address;
import com.entreck.pos.domain.valueobject.Contact;
import com.entreck.pos.domain.valueobject.GeoLocation;
import com.entreck.pos.domain.valueobject.OpeningHours;
import com.entreck.pos.domain.valueobject.WeeklyWindow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Application-layer mapper for PointOfSale domain objects and DTOs.
 *
 * <p>Uses MapStruct for automatic mapping between domain aggregates and
 * response DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PointOfSaleMapper {

  @Mapping(target = "id", expression = "java(pos.id().value())")
  @Mapping(target = "name", expression = "java(pos.name())")
  @Mapping(target = "status", expression = "java(pos.status().name())")
  @Mapping(target = "shopId", expression = "java(pos.shopId().value())")
  @Mapping(target = "createdAt", expression = "java(pos.createdAt())")
  @Mapping(target = "updatedAt", expression = "java(pos.updatedAt())")
  PointOfSaleResponse toResponse(PointOfSale pos);

  @Mapping(target = "id", expression = "java(pos.id().value())")
  @Mapping(target = "name", expression = "java(pos.name())")
  @Mapping(target = "address", expression = "java(toAddressDto(pos.address()))")
  @Mapping(target = "location", expression = "java(toGeoLocationDto(pos.geoLocation()))")
  @Mapping(target = "contact", expression = "java(toContactDto(pos.contact()))")
  @Mapping(target = "openingHours", expression = "java(toOpeningHoursDto(pos.openingHours()))")
  @Mapping(target = "status", expression = "java(pos.status().name())")
  @Mapping(target = "shopId", expression = "java(pos.shopId().value())")
  @Mapping(target = "createdAt", expression = "java(pos.createdAt())")
  @Mapping(target = "updatedAt", expression = "java(pos.updatedAt())")
  PointOfSaleDetailResponse toDetailResponse(PointOfSale pos);

  default AddressDto toAddressDto(Address address) {
    if (address == null) return null;
    return new AddressDto(
        address.street(),
        address.city(),
        address.state(),
        address.zipCode(),
        address.country());
  }

  default GeoLocationDto toGeoLocationDto(GeoLocation geoLocation) {
    if (geoLocation == null) return null;
    return new GeoLocationDto(geoLocation.latitude(), geoLocation.longitude());
  }

  default ContactDto toContactDto(Contact contact) {
    if (contact == null) return null;
    return new ContactDto(contact.email(), contact.phone());
  }

  default OpeningHoursDto toOpeningHoursDto(OpeningHours openingHours) {
    if (openingHours == null) return null;
    return new OpeningHoursDto(
        openingHours.windows().stream()
            .map(w -> new OpeningHoursDto.WeeklyWindowDto(w.dayOfWeek(), w.open(), w.close()))
            .toList(),
        openingHours.timezone());
  }

  default Address toAddress(AddressDto dto) {
    if (dto == null) return null;
    return new Address(dto.street(), dto.city(), dto.state(), dto.zipCode(), dto.country());
  }

  default GeoLocation toGeoLocation(GeoLocationDto dto) {
    if (dto == null) return null;
    return new GeoLocation(dto.latitude(), dto.longitude());
  }

  default Contact toContact(ContactDto dto) {
    if (dto == null) return null;
    return new Contact(dto.email(), dto.phone());
  }

  default OpeningHours toOpeningHours(OpeningHoursDto dto) {
    if (dto == null) return null;
    return new OpeningHours(
        dto.windows().stream()
            .map(w -> new WeeklyWindow(w.dayOfWeek(), w.open(), w.close()))
            .toList(),
        dto.timezone());
  }
}
