# Event Specification

## Requirements

### Requirement: Event lifecycle (O01, O02)

The system MUST create events and permit partial updates while enforcing required fields and valid lifecycle transitions.

#### Scenario: Create an event
- GIVEN a valid event request and organizer ID
- WHEN the client sends `POST /api/v1/events`
- THEN the system returns `201` with the event representation and its initial status

#### Scenario: Reject invalid update
- GIVEN an event update with a blank name or invalid category/date
- WHEN the client sends `PATCH /api/v1/events/{id}`
- THEN the system returns `400 VALIDATION_ERROR` with field errors and does not change the event

### Requirement: Unique event names

The system MUST reject a create or update that conflicts with an existing event name.

#### Scenario: Duplicate name
- GIVEN another event already has the requested name
- WHEN the client creates or renames an event
- THEN the system returns `422 DUPLICATE_EVENT_NAME`

### Requirement: Event search and pagination (B01, B02)

The system MUST search events by optional name and category filters and return a page envelope; requested page size MUST be capped at 100.

#### Scenario: Filtered page
- GIVEN matching events exist
- WHEN the client sends `GET /api/v1/events?name=music&category=CONCERT&page=0&size=20`
- THEN the system returns `200` with matching summaries and page metadata

#### Scenario: Oversized page
- GIVEN the client requests `size=1000`
- WHEN the search runs
- THEN the effective page size is at most 100

### Requirement: Event detail (B03)

The system MUST return the requested event or a structured not-found error.

#### Scenario: Existing event
- GIVEN an event exists for the supplied ID
- WHEN the client sends `GET /api/v1/events/{id}`
- THEN the system returns `200` with event detail

#### Scenario: Missing event
- GIVEN no event exists for the supplied ID
- WHEN the client requests its detail
- THEN the system returns `404 EVENT_NOT_FOUND`
