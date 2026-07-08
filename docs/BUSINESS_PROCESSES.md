# Business Processes and UML Diagrams

This document describes the main business processes, domain rules, and notification requirements of eTorg. It is intended to show the system from a business analysis perspective: actors, use cases, lot lifecycle, bidding rules, notification flows, and traceability between functional requirements and implemented behavior.

## Business Context

eTorg is an online auction system. A seller creates a lot, buyers place bids, and the system determines whether the lot is closed with a winner or drawn without a winner. The core auction service owns users, lots, bids, and domain rules. The notification microservice receives RabbitMQ events and sends email notifications to subscribed users.

## Actors and Use Cases

```mermaid
flowchart LR
    Buyer["Buyer"]
    Seller["Seller / Lot Owner"]
    Admin["Admin"]
    Scheduler["System Scheduler"]
    NotificationService["Notifications Microservice"]

    subgraph Core["Core Auction Service"]
        Register["Register account"]
        Login["Sign in"]
        CreateLot["Create lot"]
        BrowseLots["Browse lot cards"]
        ViewLot["View lot details"]
        MakeBid["Place bid"]
        CloseLot["Close lot by owner"]
        DrawLot["Cancel / draw lot by owner"]
        TimeoutProcessing["Process timed out lots"]
        ManageUsers["Manage user roles and accounts"]
        PublishEvents["Publish domain events"]
    end

    subgraph Notifications["Notification Process"]
        StoreSubscriber["Store email subscriber"]
        SendBidEmails["Send bid notification emails"]
        SendClosedEmails["Send lot closed emails"]
        SendDrawEmails["Send lot draw emails"]
    end

    Buyer --> Register
    Buyer --> Login
    Buyer --> BrowseLots
    Buyer --> ViewLot
    Buyer --> MakeBid

    Seller --> Register
    Seller --> Login
    Seller --> CreateLot
    Seller --> CloseLot
    Seller --> DrawLot

    Admin --> ManageUsers
    Scheduler --> TimeoutProcessing

    Register --> PublishEvents
    MakeBid --> PublishEvents
    CloseLot --> PublishEvents
    DrawLot --> PublishEvents
    TimeoutProcessing --> PublishEvents

    PublishEvents --> NotificationService
    NotificationService --> StoreSubscriber
    NotificationService --> SendBidEmails
    NotificationService --> SendClosedEmails
    NotificationService --> SendDrawEmails
```

## Domain Model

```mermaid
classDiagram
    class User {
        UUID id
        String username
        String email
        String role
    }

    class LotAggregate {
        UUID id
        UUID ownerId
        String title
        String description
        String currency
        BigDecimal minBid
        LocalDateTime timeout
        StatusEnum state
        makeBid(BidVO)
        closeByOwner(UUID)
        drawByOwner(UUID)
        changeStateAfterTimeout()
    }

    class BidVO {
        UUID id
        UUID buyerId
        String currency
        BigDecimal value
    }

    class StatusEnum {
        OPEN
        CLOSED
        DRAW
        NOT_ACTIVATED
    }

    class Event {
        <<interface>>
    }

    class UserRegisteredEvent {
        UUID userId
        String username
        String email
    }

    class BidMakedEvent {
        UUID lotId
        UUID ownerId
        String title
        List~BidVO~ bids
    }

    class LotClosedEvent {
        UUID lotId
        UUID winnerId
        String reason
        List~BidVO~ bids
        UUID ownerId
        String title
    }

    class LotDrawedEvent {
        UUID lotId
        String reason
        List~BidVO~ bids
        UUID ownerId
        String title
    }

    User "1" --> "0..*" LotAggregate : owns
    User "1" --> "0..*" BidVO : places
    LotAggregate "1" --> "0..*" BidVO : contains
    LotAggregate --> StatusEnum : has state
    Event <|.. BidMakedEvent
    Event <|.. LotClosedEvent
    Event <|.. LotDrawedEvent
    UserRegisteredEvent ..> User : created after registration
```

## Lot Lifecycle State Machine

```mermaid
stateDiagram-v2
    [*] --> OPEN: create lot

    OPEN --> OPEN: valid bid placed
    OPEN --> CLOSED: owner closes lot\nand at least one bid exists
    OPEN --> CLOSED: timeout expired\nand at least one bid exists
    OPEN --> DRAW: owner cancels / draws lot
    OPEN --> DRAW: timeout expired\nand no bids exist

    CLOSED --> [*]
    DRAW --> [*]
```

### Lot Lifecycle Rules

| ID | Rule |
| --- | --- |
| LOT-1 | A lot is created with currency, end time, and minimum bid. After creation it is `OPEN`. |
| LOT-2 | A bid can be placed only when the lot is `OPEN`, the currency matches, and the bid satisfies the minimum allowed value. |
| LOT-3 | A lot becomes `CLOSED` when it has at least one bid and is closed by the owner or by timeout. |
| LOT-4 | A lot becomes `DRAW` when it is canceled by the owner or timeout expires without bids. |
| LOT-INV-1 | The lot end time cannot be earlier than now and cannot be later than 6 months from creation. |
| LOT-INV-2 | A lot must always be in a valid state: `OPEN`, `CLOSED`, or `DRAW`. |

## Bid Placement Business Process

```mermaid
flowchart TD
    Start["Buyer submits bid"] --> LoadLot["Load lot aggregate"]
    LoadLot --> IsOpen{"Lot state is OPEN?"}
    IsOpen -- "No" --> RejectState["Reject bid: lot is not open"]
    IsOpen -- "Yes" --> Timeout{"Timeout expired?"}
    Timeout -- "Yes" --> RejectTimeout["Reject bid: bidding time expired"]
    Timeout -- "No" --> Currency{"Bid currency matches lot currency?"}
    Currency -- "No" --> RejectCurrency["Reject bid: invalid currency"]
    Currency -- "Yes" --> Value{"Bid value >= minimum allowed bid?"}
    Value -- "No" --> RejectValue["Reject bid: bid is too low"]
    Value -- "Yes" --> AddBid["Add bid to lot"]
    AddBid --> IncreaseMinBid["Increase next minimum bid by 5%"]
    IncreaseMinBid --> SaveLot["Save lot and bid"]
    SaveLot --> PublishEvent["Publish BidMakedEvent"]
    PublishEvent --> Notify["Notification service emails owner and previous bidders"]
    Notify --> End["Bid process completed"]
```

## User Registration Notification Flow

```mermaid
sequenceDiagram
    participant User
    participant Core as Core Auction Service
    participant Users as Users Module
    participant DB as Users Schema
    participant Rabbit as RabbitMQ lot.direct
    participant Queue as users.notifications
    participant Notifications as Notifications Microservice
    participant NDB as Notifications Schema

    User->>Core: Sign up
    Core->>Users: Register user
    Users->>DB: Save user
    Users->>Rabbit: Publish UserRegisteredEvent
    Rabbit->>Queue: Route by routing.users.notifications
    Queue-->>Notifications: Consume UserRegisteredEvent
    Notifications->>NDB: Save email subscriber
```

## Bid Notification Flow

```mermaid
sequenceDiagram
    participant Buyer
    participant Core as Core Auction Service
    participant Lot as Lot Module
    participant Rabbit as RabbitMQ lot.direct
    participant Queue as lot.bid.notifications
    participant Notifications as Notifications Microservice
    participant Mail as Ethereal SMTP

    Buyer->>Core: Place bid
    Core->>Lot: Validate bid business rules
    Lot-->>Core: BidMakedEvent
    Core->>Rabbit: Publish BidMakedEvent
    Rabbit->>Queue: Route by routing.lot.bid
    Queue-->>Notifications: Consume BidMakedEvent
    Notifications->>Mail: Email lot owner
    Notifications->>Mail: Email previous bidders except new bidder
```

## Lot Closed Notification Flow

```mermaid
sequenceDiagram
    participant Owner
    participant Scheduler
    participant Core as Core Auction Service
    participant Lot as Lot Module
    participant Rabbit as RabbitMQ lot.direct
    participant Queue as lot.closed.notifications
    participant Notifications as Notifications Microservice
    participant Mail as Ethereal SMTP

    Owner->>Core: Close lot manually
    Scheduler->>Core: Close timed out lot with bids
    Core->>Lot: Change state to CLOSED
    Lot-->>Core: LotClosedEvent
    Core->>Rabbit: Publish LotClosedEvent
    Rabbit->>Queue: Route by routing.lot.closed
    Queue-->>Notifications: Consume LotClosedEvent
    Notifications->>Mail: Email owner with winner and final price
    Notifications->>Mail: Email winner with final price
    Notifications->>Mail: Email other bidders with final price
```

## Lot Draw Notification Flow

```mermaid
sequenceDiagram
    participant Owner
    participant Scheduler
    participant Core as Core Auction Service
    participant Lot as Lot Module
    participant Rabbit as RabbitMQ lot.direct
    participant Queue as lot.drawed.notifications
    participant Notifications as Notifications Microservice
    participant Mail as Ethereal SMTP

    Owner->>Core: Cancel / draw lot manually
    Scheduler->>Core: Draw timed out lot without bids
    Core->>Lot: Change state to DRAW
    Lot-->>Core: LotDrawedEvent
    Core->>Rabbit: Publish LotDrawedEvent
    Rabbit->>Queue: Route by routing.lot.drawed
    Queue-->>Notifications: Consume LotDrawedEvent
    Notifications->>Mail: Email owner and bidders that no winner exists
```

## Notification Functional Requirements

The following requirements are based on `notifications-microservice/functional requirements lot - notifications.txt`.

| ID | Requirement | Event / Queue |
| --- | --- | --- |
| LOT-FR-1 | Every lot status change or new bid must notify subscribed users who placed bids on the lot and the lot owner. Each lot email includes lot link and lot title. | `BidMakedEvent`, `LotClosedEvent`, `LotDrawedEvent` |
| LOT-FR-2 | When a lot is closed, the owner receives winner username and final sale price; the winner receives the final sale price; other bidders receive closure information and final price. | `LotClosedEvent` / `lot.closed.notifications` |
| LOT-FR-3 | When a new bid is placed, previous bidders except the new bidder and the lot owner receive the new bidder username and bid amount. | `BidMakedEvent` / `lot.bid.notifications` |
| LOT-FR-4 | If the auction ends without a winner, the owner and bidders receive an email stating that nobody won. | `LotDrawedEvent` / `lot.drawed.notifications` |

## Requirement Traceability

| Business Rule / Requirement | Implemented By | Documentation Diagram |
| --- | --- | --- |
| Lot lifecycle states | `LotAggregate`, `StatusEnum` | Lot Lifecycle State Machine |
| Bid validation | `LotAggregate.makeBid`, `BidVO` | Bid Placement Business Process |
| User registration notification | `AuthenticationService`, `UserRegisteredEvent`, `UserRegistrationListener` | User Registration Notification Flow |
| Bid notification | `BidMakedEvent`, `EmailSender` | Bid Notification Flow |
| Lot closed notification | `LotClosedEvent`, `EmailSender` | Lot Closed Notification Flow |
| Lot draw notification | `LotDrawedEvent`, `EmailSender` | Lot Draw Notification Flow |
