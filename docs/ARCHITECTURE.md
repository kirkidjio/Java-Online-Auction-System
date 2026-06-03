# Architecture

eTorg uses Hexagonal Architecture. The codebase separates the auction business rules from transport and persistence details by organizing the project around domain objects, application services, ports, and adapters.

## High-Level View

```mermaid
flowchart TB
    subgraph Inbound["Inbound Adapters"]
        REST["REST Controllers"]
        Scheduler["Scheduler"]
    end

    subgraph Application["Application Layer"]
        LotService["LotService"]
        AuthService["AuthenticationService"]
        UserService["UserManagmentService"]
    end

    subgraph Domain["Domain Core"]
        LotAggregate["LotAggregate"]
        BidVO["BidVO"]
        Events["Domain Events"]
        Rules["Auction Rules"]
    end

    subgraph Ports["Outbound Ports"]
        LotRepoPort["ILotRepository"]
        LotQueryPort["ILotQueryRepository"]
        UserRepo["UserRepository"]
    end

    subgraph Adapters["Outbound Adapters"]
        LotJdbc["LotJdbcRepository"]
        LotQueryJdbc["LotQueryJdbcRepository"]
        JpaUserRepo["Spring Data UserRepository"]
    end

    DB["PostgreSQL"]

    REST --> LotService
    Scheduler --> LotService
    LotService --> LotAggregate
    LotService --> LotRepoPort
    LotService --> LotQueryPort
    LotRepoPort --> LotJdbc
    LotQueryPort --> LotQueryJdbc
    LotJdbc --> DB
    LotQueryJdbc --> DB

    REST --> AuthService
    REST --> UserService
    AuthService --> UserRepo
    UserService --> UserRepo
    UserRepo --> JpaUserRepo
    JpaUserRepo --> DB
```

## Domain Core

The domain core contains the auction model and rules. It is centered around `LotAggregate`.

Responsibilities:

- Maintain lot state
- Validate bids
- Enforce currency rules
- Enforce timeout rules
- Close lots
- Draw/cancel lots
- Produce domain events

Main classes:

- `LotAggregate`
- `BidVO`
- `StatusEnum`
- `DomainLotException`
- `DomainBidVOException`
- `BidMakedEvent`
- `LotClosedEvent`
- `LotDrawedEvent`

## Application Layer

Application services coordinate use cases. They receive DTOs from inbound adapters, load aggregates through ports, execute domain methods, and save the result.

Main classes:

- `LotService`
- `AuthenticationService`
- `UserManagmentService`

Example lot use cases:

- create lot
- make bid
- close lot by owner
- draw lot by owner
- read lot cards
- read lot details
- delete lot
- get categories

## Ports

Ports define what the application layer needs from external systems.

### `ILotRepository`

Command-side persistence port for `LotAggregate`.

Operations:

- find lot by id
- find lots with expired timeout
- save one aggregate
- save multiple aggregates
- delete lot

### `ILotQueryRepository`

Query-side persistence port for read models.

Operations:

- get sorted lot cards
- get lot details
- get categories

## Adapters

Adapters connect the application to the outside world.

### Inbound Adapters

- `LotRestController` exposes lot operations over HTTP.
- `AuthController` exposes signup and signin endpoints.
- `UserManagmentController` exposes admin user operations.
- `LotScheduler` triggers timeout-based lot state changes.

### Outbound Adapters

- `LotJdbcRepository` persists and restores auction aggregates.
- `LotQueryJdbcRepository` builds read models for API responses.
- `UserRepository` persists users through Spring Data JPA.

## Module Overview

### Lot Module

The lot module contains the auction business logic and persistence adapters. It is the main domain module of the system.

Package roots:

- `io.github.etorg.lot.api`
- `io.github.etorg.lot.internal.domain`
- `io.github.etorg.lot.internal.service`
- `io.github.etorg.lot.internal.infrastructure`

### Users Module

The users module contains authentication and user management logic.

Package roots:

- `io.github.etorg.users.api`
- `io.github.etorg.users.service`
- `io.github.etorg.users.security`
- `io.github.etorg.users.infrastructure`
- `io.github.etorg.users.models`

## Request Flow: Make Bid

```mermaid
sequenceDiagram
    participant Client
    participant Controller as LotRestController
    participant Service as LotService
    participant Repo as ILotRepository
    participant Domain as LotAggregate
    participant DB as PostgreSQL

    Client->>Controller: POST /api/lots/makebid/
    Controller->>Service: makeBid(userId, dto)
    Service->>Repo: findById(lotId)
    Repo->>DB: SELECT lot and bids
    DB-->>Repo: data
    Repo-->>Service: LotAggregate
    Service->>Domain: makeBid(BidVO)
    Domain->>Domain: validate state, timeout, currency, value
    Domain-->>Service: updated aggregate
    Service->>Repo: save(lot)
    Repo->>DB: UPSERT lot and bids
    Service-->>Controller: success
    Controller-->>Client: 201
```

## Design Goals

- Keep auction rules inside the domain model.
- Keep controllers thin.
- Keep persistence behind repository ports.
- Separate command-side aggregate persistence from query-side read models.
- Make the codebase easy to explain during technical interviews.
