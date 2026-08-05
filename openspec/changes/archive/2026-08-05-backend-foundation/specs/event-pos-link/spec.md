# Event-POS Link Specification

## Requirements

### Requirement: Associate and dissociate POS (O03, O04)

The system MUST associate an existing PointOfSale with an existing Event, reject duplicate links, and allow the organizer to dissociate the link.

#### Scenario: Associate POS
- GIVEN both aggregates exist and no link exists
- WHEN the client sends `POST /api/v1/events/{eventId}/points-of-sale`
- THEN the system returns `201` with the link and default availability

#### Scenario: Duplicate association
- GIVEN a link already exists for the event and POS pair
- WHEN the client repeats the association
- THEN the system returns `422 LINK_ALREADY_EXISTS` and keeps one link

#### Scenario: Dissociate POS
- GIVEN a link exists
- WHEN the client sends `DELETE /api/v1/events/{eventId}/points-of-sale/{posId}`
- THEN the system returns `204` and removes the link

### Requirement: List linked POS (B04)

The system MUST return the event's linked POS records with link availability and pagination metadata.

#### Scenario: List links
- GIVEN an event has linked POS records
- WHEN the client sends `GET /api/v1/events/{eventId}/points-of-sale`
- THEN the system returns `200` with each POS and its link status

### Requirement: Update availability (S03)

The system MUST replace availability and optional note for an existing event-POS link using the availability contract selected during reconciliation.

#### Scenario: Update availability
- GIVEN the shop owns an existing link
- WHEN the client sends `PUT /api/v1/points-of-sale/{posId}/events/{eventId}/availability`
- THEN the system returns `200` with the new status, note, and `updatedAt`

#### Scenario: Missing link
- GIVEN no link exists for the event and POS pair
- WHEN availability is updated
- THEN the system returns `404 LINK_NOT_FOUND`

### Requirement: Link integrity

The system MUST enforce `(eventId, posId)` uniqueness in both application behavior and persistence.

#### Scenario: Concurrent duplicate insert
- GIVEN two requests attempt the same pair concurrently
- WHEN persistence completes
- THEN at most one link exists and the losing request maps to `422 LINK_ALREADY_EXISTS`

#### Scenario: Availability reconciliation
- GIVEN the design/tasks use `AVAILABLE/LIMITED/SOLD_OUT/UNKNOWN` but an earlier locked scope uses available/unavailable/unknown
- WHEN implementation begins
- THEN the team records the chosen enum/API contract before coding
