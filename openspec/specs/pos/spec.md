# PointOfSale Specification

## Requirements

### Requirement: POS registration and update (S01, S02)

The system MUST register and partially update a PointOfSale after validating identity, address, contact, location, and opening-hours fields.

#### Scenario: Register valid POS
- GIVEN a valid shop-owned POS request
- WHEN the client sends `POST /api/v1/points-of-sale`
- THEN the system returns `201` with the persisted POS representation

#### Scenario: Reject invalid location
- GIVEN latitude is outside `[-90,90]` or longitude outside `[-180,180]`
- WHEN the client registers or updates the POS
- THEN the system returns `400 VALIDATION_ERROR` naming the invalid field

### Requirement: POS value-object validation

The system MUST reject invalid address, contact, country, and weekly-window values before persistence.

#### Scenario: Invalid contact or hours
- GIVEN an invalid email, phone, country code, empty street, or `open >= close`
- WHEN the request is processed
- THEN the system returns `400` with field errors and no partial aggregate is stored

### Requirement: POS detail (B06)

The system MUST return complete POS details or a structured not-found error.

#### Scenario: Existing POS
- GIVEN a POS exists for the supplied ID
- WHEN the client sends `GET /api/v1/points-of-sale/{id}`
- THEN the system returns `200` with address, hours, contact, location, and status

#### Scenario: Missing POS
- GIVEN no POS exists for the supplied ID
- WHEN the client requests its detail
- THEN the system returns `404 POS_NOT_FOUND`
