
# Backend Application for Online Auction

This backend service powers an online auction platform. It allows users to create lots for sale, place bids, and determine winners.

## Highlights

- **Event-driven microservices architecture** – enables easy code modifications and loose coupling between services.
- **Cursor-based pagination** – fetch lot cards sorted by customizable attributes efficiently.
- **JWT authentication** – secure access for users.
- **Email notifications** – keeps participants informed about lot status changes.

## Overview

This project was developed as part of a learning experience to explore modern technologies and architectural patterns. It incorporates Domain-Driven Design (DDD) and Test-Driven Development (TDD) for building robust business logic, and uses events to reduce coupling between microservices. This approach allowed me to continue working on the project beyond the initial two-week timeframe :) .

##  Stack

- Java 21
- Kotlin
- Spring Boot (Web MVC, Security, JPA, JDBC, AMQP, Mail)
- PostgreSQL
- RabbitMQ
- Flyway
- JWT
- Lombok
- JUnit 5
- Springdoc OpenAPI
- Maven

## Author

I'm Mykhailo Pashuk – the lead business analyst, architect, and programmer behind this project :) . I'm also a student at Bialystok University of Technology, passionate about Java and system design.

## Planned
- Docker support
- microservice for moderators
- email confirmation for user registration
- caching of the most popular lots


## Main Endpoints

Authentication:

- `POST /api/users/authentication/signup`
- `POST /api/users/authentication/signin`

Lots:

- `PUT /api/lots/create/`
- `POST /api/lots/makebid/`
- `GET /api/lots/cards/`
- `GET /api/lots/item/{id}`
- `DELETE /api/lots/item/delete/{id}`
- `GET /api/lots/categories`



## Example Requests

### Sign Up

```http
POST /api/users/authentication/signup
Content-Type: application/json

{
  "email": "john@example.com",
  "username": "john_auction",
  "password": "Password1!"
}
```

### Sign In

```http
POST /api/users/authentication/signin
Content-Type: application/json

{
  "username": "john_auction",
  "password": "Password1!"
}
```

Response:

```json
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Create Lot

```http
PUT /api/lots/create/
Authorization: Bearer <jwt>
Content-Type: application/json

{
  "currency": "PLN",
  "timeout": "2026-08-01T18:00:00",
  "description": "Vintage mechanical watch in good condition",
  "minBid": 100.00,
  "title": "Vintage Watch"
}
```

### Make Bid

```http
POST /api/lots/makebid/
Authorization: Bearer <jwt>
Content-Type: application/json

{
  "lotId": "61b04e77-df64-4a07-b4a7-4c9d6f0ac121",
  "currency": "PLN",
  "value": 150.00
}
```

### Get Lot Cards

```http
GET /api/lots/cards/?attribute=MIN_BID&order=ASC
```

```