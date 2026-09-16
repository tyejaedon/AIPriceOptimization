# Firestore Client Access

This document describes how the Android client is allowed to touch
Firestore. The backend Firestore schema (owned by the Python model
repository) contains top-level collections:

```
mentors
macro_indices
industry_partitions
service_listings
mpesa_tariffs
historical_transactions
```

## Principle

**Firestore is not a replacement for the pricing API.** The FastAPI service
remains the only writer of official historical transactions and the only
caller of the ML pipeline. The mobile client's Firestore access is limited to
its own user-scoped data.

## Recommended user-scoped paths

```
mentors/{mentorId}
mentors/{mentorId}/quote_history/{transactionId}
mentors/{mentorId}/drafts/{draftId}
mentors/{mentorId}/preferences/{preferenceId}
```

The server projects a copy of each successful `historical_transactions`
record into `mentors/{mentorId}/quote_history/{transactionId}` after a
prediction succeeds, so the client never needs broad read access to the
global collection.

## Ownership matrix

| Data | Owner | Mobile access |
|---|---|---|
| `mentors/{mentorId}` | Authenticated user / server | Read + update own profile |
| `macro_indices` | Backend administrator | Read-only, only if required by UI |
| `industry_partitions` | Backend administrator | Read-only |
| `service_listings` | Backend | No direct mobile access |
| `mpesa_tariffs` | Backend administrator | Read-only or API-mediated |
| `historical_transactions` | Backend | No direct unrestricted access |
| `mentors/{id}/quote_history` | Authenticated user / server | Read own records |
| `mentors/{id}/drafts` | Authenticated user | Read + write own drafts |

## Starting security rules

See `firebase/firestore.rules` for a starting point. It is **not** a
production-ready policy — it must be validated with the Firebase Emulator
Suite before any release, and ownership must always be checked against
`request.auth.uid`, never a client-supplied `mentor_id`.

## Non-negotiables

- Never trust a client-supplied `mentor_id`.
- Never allow the client to create/modify official historical transactions.
- Never allow the client to modify prediction values.
- Never embed a Firebase service-account key in this repository.
- Use Firebase App Check where available.
- Test rules with the Firebase Emulator Suite (`docs/Release_Runbook.md`
  documents the local emulator commands once configured).

