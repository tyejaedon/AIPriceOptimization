# API Contract

This document is the mobile client's copy of the backend contract. It mirrors
what is documented in the Python model repository's `README.md` and
`docs/Blueprint.md`. The mobile app must treat this file — not assumptions —
as the source of truth for what the server currently supports.

> **Status note:** the backend repository documents `src.serve:app`, but at
> the time this mobile repo was bootstrapped, `src/serve.py` was not present
> in that workspace. Treat the exact deployed URL, auth configuration, and
> any endpoints beyond the two below as environment-specific / unconfirmed
> until verified against the running server.

## Confirmed endpoints

```
GET  /health
POST /api/v1/optimize-price
```

### `GET /health`

Response:

```json
{
  "status": "HEALTHY",
  "models_loaded": true
}
```

### `POST /api/v1/optimize-price`

Request:

```json
{
  "raw_description": "Senior Android engineer specializing in Kotlin coroutines, Jetpack Compose UI architecture, and clean MVVM modularization. Mentored 15+ junior developers in TDD.",
  "selected_industry": "SOFTWARE_ENG",
  "mentor_country": "KE",
  "client_country": "US",
  "competitiveness_score": 0.65,
  "market_saturation_score": 0.45
}
```

Response:

```json
{
  "base_predicted_rate": 4700.0,
  "mpesa_tariff_surcharge": 55.0,
  "final_quoted_rate": 4755.0,
  "currency": "KES",
  "bilateral_arbitrage_factor": 0.51,
  "nearest_neighbors": [
    {
      "peer_index": 1402,
      "distance": 0.214,
      "verified_rate": 4900.0,
      "similarity_score": 0.823
    }
  ]
}
```

## HTTP status → mobile behavior

| Status | Mobile behavior |
|---|---|
| `401` | Refresh Firebase ID token or route to sign-in |
| `403` | Show an authorization error |
| `422` | Show field validation feedback |
| `429` | Ask the user to retry later |
| `500` | Show a recoverable server error and preserve the draft |
| `503` | Show that the pricing service is currently unavailable |
| Timeout | Offer retry without losing form data |
| No network | Save draft locally, allow retry |

Never discard the pricing form on failure.

## Undocumented / speculative endpoints

The following are **not confirmed** to exist server-side. Do not call them
from the app until they are verified against the backend repository, and
gate any client code behind an interface so it degrades gracefully:

```
GET /api/v1/profile
PUT /api/v1/profile
GET /api/v1/industries
GET /api/v1/countries
GET /api/v1/quote-history
GET /api/v1/quote-history/{transaction_id}
```

## Authentication

Every protected request must carry a Firebase ID token:

```
Authorization: Bearer <firebase-id-token>
```

The server is expected to validate the token; the client never stores it in
plain text and always requests a fresh token through the Firebase SDK.

## Kotlin DTOs

See `app/src/main/java/com/tyejaedon/aipriceoptimization/data/remote/dto/ApiDtos.kt`
and `PricingApi.kt` for the canonical Kotlin representation of this contract.
Keep this document and those files in sync.

