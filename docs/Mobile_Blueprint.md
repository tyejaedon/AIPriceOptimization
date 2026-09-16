# Mobile Blueprint

This is the authoritative architecture reference for the
`com.tyejaedon.aipriceoptimization` Android client. It condenses the full
planning discussion into an actionable, living document. Update it whenever
architecture decisions change.

## 1. System context

The production pricing model runs **server-side only**. It is not, and must
not be, reimplemented in the Android app. Per the model repository:

```
Profile description and skills
   -> Text sanitation -> TF-IDF -> Truncated SVD -> 50-d text vector
Mentor/client country, competitiveness, saturation
   -> 3-d normalized metadata vector
50-d + 3-d -> 53-d hybrid coordinate
   -> Industry-partitioned KD-Tree -> KNN / IDW -> base predicted rate
   -> M-Pesa tariff evaluator -> final quoted rate
```

The Android app's job is presentation, authentication, request composition,
result visualization, and local history/draft management. See
`docs/API_Contract.md` for the exact request/response shapes.

## 2. Architecture style

```
Jetpack Compose screens
        |
Screen-level ViewModels (Hilt, StateFlow)
        |
Use cases (domain/usecase)
        |
Repository interfaces (domain/repository)
        |
   +----+----------------------+
   |                           |
Retrofit (data/remote)   Firebase Auth / Firestore (Phase 2+)
   |                           |
FastAPI                   User profile & history
```

Layering, already scaffolded under `app/src/main/java/com/tyejaedon/aipriceoptimization`:

```
core/
  common/       AppResult<T> - generic success/failure wrapper
  error/        AppError - stable UI-facing error categories
  dispatchers/  Hilt-provided CoroutineDispatchers for testability
  navigation/   NavRoutes + AppNavHost (Navigation Compose)
  designsystem/ (reserved) shared Compose components
  security/     (reserved) Phase 2 - Keystore-backed encryption helpers
  validation/   (reserved) Phase 4 - pricing form validators

data/
  remote/       PricingApi, DTOs, NetworkModule (Retrofit/OkHttp/kotlinx.serialization)
  local/        (reserved) Phase 6 - Room database, drafts, history cache
  firestore/    (reserved) Phase 3 - FirestoreDataSource, mappers
  repository/   Repository implementations (HealthRepositoryImpl today)

domain/
  model/        Domain models decoupled from server field-naming (HealthStatus today)
  repository/   Repository interfaces
  usecase/      Single-purpose use cases (GetHealthStatusUseCase today)

feature/
  splash/       Bounded health check, routes to dashboard
  dashboard/    Health indicator + inert "create recommendation" entry point
  auth/         (reserved) Phase 2
  pricing/      (reserved) Phase 4
  history/      (reserved) Phase 6
  profile/      (reserved) Phase 3
  settings/     (reserved) Phase 6/7

di/             Hilt modules binding interfaces to implementations
```

## 3. Navigation

Current routes (`core/navigation/NavRoutes.kt`):

```
/splash
/dashboard
```

Planned routes, added as each phase lands:

```
/auth/sign-in
/auth/register
/auth/forgot-password
/onboarding/profile
/onboarding/country
/pricing/new
/pricing/result/{requestId}
/history
/history/{quoteId}
/profile
/settings
/about
```

**Rule:** never pass a full domain object (e.g. a pricing recommendation)
through a navigation argument. Pass an ID and resolve it via a
repository-backed state holder.

## 4. Screens (target set)

1. Splash - bounded API health check, routes onward. *(implemented, simplified)*
2. Sign in / Register / Forgot password - Firebase Authentication. *(Phase 2)*
3. Onboarding: profile + country setup. *(Phase 3)*
4. Dashboard - entry point, health indicator, recent activity. *(implemented, simplified)*
5. Pricing input - the primary workflow. *(Phase 4)*
6. Loading/submission - staged progress messaging, cancellable. *(Phase 4)*
7. Recommendation result - base rate / surcharge / final rate shown
   separately, plus peer explainability. *(Phase 5)*
8. Quote history + detail. *(Phase 6)*
9. Profile, Settings, About. *(Phase 6/7)*

## 5. Data flow rules

- The backend response is always authoritative. Client-side validation is
  for usability only, never a substitute for server validation.
- Money is never manipulated as raw floating point client-side for display
  math; use `BigDecimal`-backed domain types once the `Money` value type is
  introduced in Phase 4.
- Peer `peer_index` is a model-record index, not a person identifier — never
  present it as one in the UI.
- Errors are mapped into `core.error.AppError` at the repository boundary
  (see `data/repository/HealthRepositoryImpl.kt` for the pattern); ViewModels
  and Composables never see Retrofit/OkHttp exception types directly.

## 6. Security

- Firebase Authentication is the identity provider (Phase 2). Firebase ID
  tokens are attached to API requests as `Authorization: Bearer <token>` via
  an OkHttp interceptor (`data/remote/NetworkModule.kt` has a `TODO` marking
  where it will be added).
- HTTPS only for staging/production. Cleartext is scoped exclusively to the
  emulator loopback address `10.0.2.2` via
  `res/xml/network_security_config.xml`, used only by the `dev` flavor.
- No service-account credentials, keystores, or `google-services.json` are
  committed (`.gitignore` enforces this).
- Raw descriptions, tokens, and emails must never be logged. The
  `HttpLoggingInterceptor` is `BASIC` level in debug and `NONE` in release —
  never switch it to `BODY` in a build that could leak user input.

## 7. Offline behavior

The model is server-side only, so there is no offline authoritative
prediction. Planned (Phase 6) offline capabilities:

- View cached quote history with an explicit "stale" timestamp indicator.
- Create/edit/save drafts locally (Room).
- Preserve in-progress form state across any failed submission.

Never present a cached number as a live quote without a visible timestamp.

## 8. Testing strategy

- **Unit tests**: use-case logic, DTO/domain mapping, error mapping,
  ViewModel state transitions (MockK + Turbine + kotlinx-coroutines-test are
  already in the version catalog).
- **API contract tests**: mock `PricingApi` responses for 200/401/422/429/
  500/503/timeout/malformed payloads.
- **Firestore emulator tests** (Phase 3+): ownership rules, cross-user
  denial, official-transaction write denial.
- **Compose UI tests**: form validation, loading/error/offline states,
  accessibility semantics.

## 9. Phased delivery

See `docs/Release_Runbook.md` for build/test commands and
`.github/copilot-instructions.md` for the agent operating rules per phase.

```
Phase 1 (this bootstrap): shell, DI, navigation, theme, /health integration
Phase 2: Firebase Authentication + token interceptor
Phase 3: Mentor profile + Firestore rules/emulator tests
Phase 4: Pricing form + API integration
Phase 5: Recommendation result + peer explainability
Phase 6: Quote history + Room drafts + offline states
Phase 7: Hardening - tests, accessibility, crash reporting, release signing
```

