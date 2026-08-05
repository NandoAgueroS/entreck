# Shared Kernel Specification

## Requirements

### Requirement: Typed IDs and enums

The system MUST represent Event, PointOfSale, Organizer, Shop, and link identifiers as non-null typed Long IDs, and MUST reject null IDs. Domain enums MUST be used for event category, lifecycle/status, and availability rather than arbitrary strings.

#### Scenario: Invalid typed ID
- GIVEN a caller constructs a typed ID with null
- WHEN the value is created
- THEN construction fails with a domain validation error

### Requirement: Pagination envelope

The system MUST expose page results through a stable response containing content and page metadata, with page size capped at 100.

#### Scenario: Page response
- GIVEN a repository returns a page of results
- WHEN an endpoint serializes it
- THEN the response contains content, page, size, totalElements, and totalPages

#### Scenario: Page cap
- GIVEN a request asks for more than 100 items
- WHEN pagination is applied
- THEN the response and query use a maximum size of 100

### Requirement: Error envelope and field errors

The system MUST return a consistent error envelope with code, message, correlation ID, and optional field errors.

#### Scenario: Validation errors
- GIVEN a request fails bean validation
- WHEN the controller handles it
- THEN the response is `400 VALIDATION_ERROR` and each invalid field is represented in `fieldErrors`

### Requirement: Global exception mapping

The system MUST map known domain exceptions to stable HTTP statuses and map unexpected exceptions to a correlation-ID-bearing 500 response.

#### Scenario: Known exception
- GIVEN an event/POS/link not-found or duplicate exception
- WHEN the global handler processes it
- THEN it returns the documented 404 or 422 code without a stack trace

#### Scenario: Unexpected exception
- GIVEN an unhandled runtime failure
- WHEN the global handler processes it
- THEN it returns `500 INTERNAL_ERROR` and includes a correlation ID in the body and response header
