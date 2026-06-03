# API Reference

Base URL:

```text
http://localhost:8080
```

Protected endpoints require a JWT token:

```http
Authorization: Bearer <jwt>
```

## Authentication

### Sign Up

```http
POST /api/users/authentication/signup
Content-Type: application/json
```

Request body:

```json
{
  "email": "john@example.com",
  "username": "john_auction",
  "password": "Password1!"
}
```

Response:

```text
User registered
```

### Sign In

```http
POST /api/users/authentication/signin
Content-Type: application/json
```

Request body:

```json
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

## Lots

### Create Lot

```http
PUT /api/lots/create/
Authorization: Bearer <jwt>
Content-Type: application/json
```

Request body:

```json
{
  "currency": "PLN",
  "timeout": "2026-08-01T18:00:00",
  "description": "Vintage mechanical watch in good condition",
  "minBid": 100.00,
  "title": "Vintage Watch"
}
```

Response:

```json
201
```

### Make Bid

```http
POST /api/lots/makebid/
Authorization: Bearer <jwt>
Content-Type: application/json
```

Request body:

```json
{
  "lotId": "61b04e77-df64-4a07-b4a7-4c9d6f0ac121",
  "currency": "PLN",
  "value": 150.00
}
```

Response:

```json
201
```

### Get Lot Cards

```http
GET /api/lots/cards/?attribute=MIN_BID&order=ASC
```

Query parameters:

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| `attribute` | string | yes | Sort attribute: `MIN_BID`, `CREATED_AT`, `TIMEOUT` |
| `order` | string | yes | Sort order: `ASC`, `DESC` |
| `cursor` | string | no | Cursor value from the previous response |

Response:

```json
{
  "lotcards": [
    {
      "title": "Vintage Watch",
      "min_bid": 100.00,
      "currency": "PLN",
      "timeout": "2026-08-01T18:00:00",
      "created_at": "2026-06-01T12:00:00",
      "id": "61b04e77-df64-4a07-b4a7-4c9d6f0ac121"
    }
  ],
  "cursor": "100.00"
}
```

### Get Lot Details

```http
GET /api/lots/item/{id}
```

Response:

```json
{
  "id": "61b04e77-df64-4a07-b4a7-4c9d6f0ac121",
  "ownerId": "ecaa1291-cb0d-4681-a3d2-d42bc7e41ac6",
  "timeout": "2026-08-01T18:00:00",
  "description": "Vintage mechanical watch in good condition",
  "created_at": "2026-06-01T12:00:00",
  "min_bid": 157.50,
  "currency": "PLN",
  "status": "OPEN",
  "title": "Vintage Watch",
  "bids": [
    {
      "id": "62542531-584c-440e-937b-a8fb0fb76d36",
      "username": "john_auction",
      "currency": "PLN",
      "value": 150.00
    }
  ],
  "category": "Watches"
}
```

### Delete Lot

```http
DELETE /api/lots/item/delete/{id}
```

Response:

```text
empty response
```

### Get Categories

```http
GET /api/lots/categories
```

Response:

```json
[
  {
    "id": "9e64dd0f-1953-4cf6-b039-118639cc4e6f",
    "category": "Watches"
  }
]
```

## Admin Users

### Change User Role

```http
POST /api/admin/users/change-role
Authorization: Bearer <admin-jwt>
Content-Type: application/json
```

Request body:

```json
{
  "username": "john_auction",
  "role": "ROLE_MODERATOR"
}
```

Available roles:

- `ROLE_ADMIN`
- `ROLE_MODERATOR`
- `ROLE_USER`

Response:

```json
201
```

### Delete User

```http
DELETE /api/admin/users/delete
Authorization: Bearer <admin-jwt>
Content-Type: application/json
```

Request body:

```json
{
  "username": "john_auction"
}
```

Response:

```json
201
```
