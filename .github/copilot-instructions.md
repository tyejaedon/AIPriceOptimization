# Copilot / Coding-Agent Instructions — AI Price Optimization Android Client

You are the implementation agent for the `com.tyejaedon.aipriceoptimization`
Android repository (Kotlin + Jetpack Compose).

Your responsibility is the Android client UI tier for an existing
server-side dynamic pricing platform. The authoritative model and pricing
logic remain on the backend. **Do not recreate the Python ML pipeline
inside Android.**

## Project context

The backend recommends hourly prices for technical mentors, consultants,
and freelancers via:

1. Text sanitation.
2. TF-IDF transformation.
3. Truncated SVD reduction to a 50-dimensional text vector.
4. Country/market metadata normalization into a 3-dimensional vector.
5. Fusion into a 53-dimensional hybrid coordinate.
6. Industry-partitioned KD-Tree nearest-neighbor retrieval.
7. KNN / inverse-distance-weighted base-rate prediction.
8. M-Pesa tariff surcharge evaluation for Kenyan mentor transactions.
9. Explainable output containing peer matches and similarity scores.

Never load or execute `.joblib` files, TF-IDF vectorizers, SVD reducers,
KD-Trees, or any training artifact on-device.

The confirmed backend endpoints are documented in `docs/API_Contract.md`:

```
GET  /health
POST /api/v1/optimize-price
```

Treat any endpoint not listed there as unconfirmed. If a task requires an
endpoint that isn't documented, create an interface abstraction and a
`TODO`, and note the gap — do not assume the endpoint exists.

## Non-negotiable engineering rules

1. Kotlin, Jetpack Compose, Material 3.
2. MVVM with unidirectional data flow (`StateFlow` UI state exposed from
   `@HiltViewModel`s).
3. Hilt for dependency injection — bind interfaces in `di/`, one module per
   concern (`NetworkModule`, `RepositoryModule`, `DispatcherModule`, ...).
4. Kotlin Coroutines + Flow for async work.
5. Retrofit + kotlinx.serialization for REST (`data/remote/`).
6. Firebase Authentication for identity once Phase 2 begins; Firestore only
   through explicit repository/data-source boundaries, never directly from
   Composables or ViewModels.
7. Room for local drafts/history cache; DataStore for preferences.
8. Never commit `google-services.json`, service-account keys, keystores, or
   production secrets.
9. Never write official historical transactions directly from the mobile
   client — that remains server-mediated.
10. Never log Firebase tokens, raw descriptions, emails, or full API
    payloads. `HttpLoggingInterceptor` must stay at `BASIC` (debug) / `NONE`
    (release) — never `BODY`.
11. All money values carry an explicit currency; never rely on a bare
    number in the UI.
12. The backend response is always authoritative. Client-side validation is
    for usability only.
13. Never discard user input on a failed request — preserve it as a draft
    or leave the form populated for retry.
14. Add tests with each feature (unit tests for logic, Compose tests for
    UI, Firestore emulator tests for security rules once Firestore lands).
15. Do not modify backend/model code from this repository.
16. Follow the issue-first workflow in `CONTRIBUTING.md`.

## Repository workflow

Every code change must map to an existing GitHub issue.

Branches: `<type>/<issue-number>-<short-slug>` where type is one of
`feature`, `fix`, `chore`, `docs`, `refactor`, `test`.

Never commit directly to `main`. Open draft pull requests first. Every PR
must contain `Closes #<issue-number>` and declare its target milestone.

If an issue number is not supplied, stop and ask for one before creating a
branch or making a code change.

Before implementing a feature, read:

- `README.md`
- `docs/Mobile_Blueprint.md`
- `docs/API_Contract.md`
- `docs/Firestore_Client_Access.md`
- `docs/Release_Runbook.md`
- `CONTRIBUTING.md`

## Existing package structure (do not restructure without an issue)

```
com.tyejaedon.aipriceoptimization/
  core/
    common/       AppResult<T>
    error/        AppError
    dispatchers/  Dispatcher qualifier + DispatcherModule
    navigation/   NavRoutes, AppNavHost
    designsystem/ (reserved)
    security/     (reserved)
    validation/   (reserved)
  data/
    remote/       PricingApi, dto/, NetworkModule
    local/        (reserved — Room)
    firestore/    (reserved)
    repository/   *RepositoryImpl
  domain/
    model/
    repository/
    usecase/
  feature/
    splash/
    dashboard/
    auth/         (reserved)
    pricing/      (reserved)
    history/      (reserved)
    profile/      (reserved)
    settings/     (reserved)
  di/
```

## Screen build order (see docs/Mobile_Blueprint.md for full detail)

1. Splash — implemented (bounded health check, no auth branching yet).
2. Sign in / Register / Forgot password — Firebase Authentication.
3. Onboarding: profile + country setup.
4. Dashboard — implemented (health chip + inert CTA).
5. Pricing input form.
6. Loading/submission state.
7. Recommendation result + peer explainability.
8. Quote history + detail.
9. Profile, Settings, About.

**Never pass a full domain object through a navigation route.** Pass an
identifier and resolve state from a repository/ViewModel.

## Pricing form (once Phase 4 begins)

Required fields: raw description, selected industry, mentor country, client
country, competitiveness score (0.0–1.0), market saturation score
(0.0–1.0). Client-side validation is for usability; the server remains
authoritative.

## Result presentation (once Phase 5 begins)

Always show separately: base predicted rate, M-Pesa surcharge, final quoted
rate, currency, bilateral arbitrage factor, peer matches (with similarity
as a percentage and verified rate as currency). Never imply the price is
guaranteed — label it a recommendation. Never present `peer_index` as a
personal identifier.

## Error handling

Map every repository failure into `core.error.AppError` (see
`data/repository/HealthRepositoryImpl.kt` for the established pattern)
before it reaches a ViewModel. See `docs/API_Contract.md` for the HTTP
status → behavior table.

## Firestore (once Phase 3 begins)

Follow `docs/Firestore_Client_Access.md` exactly. User-scoped paths only
(`mentors/{mentorId}/...`). No broad reads of `mentors`, `service_listings`,
or `historical_transactions`. Validate every rule change with the Firebase
Emulator Suite before merging.

## Definition of done

- Follows the package boundaries above.
- Has a ViewModel + explicit UI state where a screen is involved.
- Handles loading, success, empty, offline, and error states as applicable.
- Preserves user input during failures.
- No sensitive values logged.
- Unit tests (and Compose/Firestore-emulator tests where applicable) pass.
- `docs/*.md` updated to reflect the change.
- Branch follows the naming convention; PR is a draft with
  `Closes #<issue-number>`.
- No secrets or generated data committed.

Start by inspecting the repository and confirming which phase (see
`docs/Mobile_Blueprint.md`, section 9) the requested issue belongs to before
writing code.

