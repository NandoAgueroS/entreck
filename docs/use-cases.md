# Entreck — MVP Use Cases

Product scope for v1: a **mobile-first directory** of physical points of sale for event tickets.
The app does **not** sell, pay for, or issue tickets in v1.

Domain terms (see also `AGENTS.md` §6): **Event**, **PointOfSale** (POS), **Stock / Open status**, **Nearby search**.

Use case class naming in code: `<Verb><Noun>UseCase` (e.g. `PublishEventUseCase`).

## Actors

| Actor | Role in v1 |
| --- | --- |
| **Buyer** | Finds where to buy tickets near them |
| **Organizer** | Publishes events and associates POS |
| **Shop** | Physical point of sale (local shop data) |

---

## Buyer

| ID | Use case | Description |
| --- | --- | --- |
| UC-B01 | `SearchEventsByName` | Search events by name |
| UC-B02 | `SearchEventsByCategory` | Filter events by category |
| UC-B03 | `GetEventDetail` | View event detail and associated POS |
| UC-B04 | `ListEventPointsOfSale` | List POS linked to a given event |
| UC-B05 | `SearchNearbyPointsOfSale` | Query POS within a radius of the user location |
| UC-B06 | `GetPointOfSaleDetail` | Show address, hours, contact, location, stock/open signal |
| UC-B07 | `ShowPointsOfSaleOnMap` | Display POS markers on the map (markers as state; map object stays in UI) |

---

## Organizer

| ID | Use case | Description |
| --- | --- | --- |
| UC-O01 | `PublishEvent` | Create/publish an event (name, category, date, description) |
| UC-O02 | `UpdateEvent` | Edit event data |
| UC-O03 | `AssociatePointOfSaleToEvent` | Link one or more POS to an event |
| UC-O04 | `DissociatePointOfSaleFromEvent` | Remove a POS association from an event |

---

## Shop / PointOfSale

| ID | Use case | Description |
| --- | --- | --- |
| UC-S01 | `RegisterPointOfSale` | Register a shop (address, opening hours, contact, geo location) |
| UC-S02 | `UpdatePointOfSale` | Edit shop information |
| UC-S03 | `UpdatePointOfSaleAvailability` | Update per-POS stock/open availability signal (data flag, not hardcoded) |

---

## Explicit non-goals (v1)

- Buying, paying for, or issuing tickets
- Cart, checkout, or payment flows
- Real ticket inventory (only a simple availability signal)
- Social login / deep organizer analytics (unless later scoped)

---

## Product decisions

### Auth / write path

**Decided:** Organizer and Shop **self-manage from the app** (authenticated write path).
Buyers remain read-oriented for discovery (search, detail, map, nearby).

### Nearby defaults

**Decided:**

| Parameter | v1 default |
| --- | --- |
| Radius | **20 km** |
| Max results | **50** (cap for map/list payload; adjustable later) |
| Center | User location at query time (lat/lon required) |

### Availability model (recommended → working decision)

v1 is a **directory signal**, not real ticket inventory. Keep stock separate from shop hours.

| Concept | Model | Owner |
| --- | --- | --- |
| Opening hours | Schedule on `PointOfSale` (e.g. weekly windows) | Shop |
| Open now | **Derived** from hours + timezone/query time (not a manual flag) | System |
| Ticket availability | Explicit per POS–Event link | Shop (or organizer if they manage that POS link) |

**Ticket availability (per Event ↔ PointOfSale association):**

```text
AvailabilityStatus: AVAILABLE | LIMITED | SOLD_OUT | UNKNOWN
note: optional short string (max ~140 chars)
updatedAt: instant of last change
```

| Status | Meaning for Buyer |
| --- | --- |
| `AVAILABLE` | Shop reports tickets available |
| `LIMITED` | Running low / few left (soft signal) |
| `SOLD_OUT` | No tickets at this POS for this event |
| `UNKNOWN` | Default when never set or stale policy applies |

**Rules (v1):**

- Availability is **per event at a POS**, not a global shop flag (a shop can be sold out for one event and available for another).
- No integer stock counts in v1 (avoids fake precision and sync pain).
- Buyer UI shows status + optional note + “updated … ago”.
- Optional later: treat `updatedAt` older than N days as display hint “may be outdated” without auto-changing the enum.

**Rejected for v1:** single boolean `hasStock`, manual `isOpen` flag (duplicates hours), and real inventory quantities.
