# AI Price Optimization — Android Client

Kotlin/Jetpack Compose Android client for a server-side dynamic pricing
platform aimed at technical mentors, consultants, and freelancers. The
authoritative pricing model (TF-IDF → Truncated SVD → 53-dimensional hybrid
vector → industry-partitioned KD-Tree → KNN/IDW → M-Pesa surcharge) runs on
a separate FastAPI backend. **This app does not, and must never, reimplement
that pipeline locally.**

See `docs/Mobile_Blueprint.md` for the full architecture, `docs/API_Contract.md`
for the backend contract this app targets, and `docs/Firestore_Client_Access.md`
for Firestore access rules.

## Status

Phase 1 bootstrap: application shell, Hilt DI, Navigation Compose, Material 3
theme, and a live `GET /health` integration. No authentication, pricing form,
or Firestore access has been implemented yet — see the phased plan in
`docs/Mobile_Blueprint.md`.

## Requirements

- Android Studio (current stable channel compatible with AGP `9.4.0`)
- JDK 25 (see `gradle/gradle-daemon-jvm.properties`; the Gradle wrapper
  provisions this toolchain automatically via `./gradlew`)
- Kotlin `2.2.10` (managed by the Gradle plugin, no local installation needed)

## Getting started

```powershell
git clone <this-repo>
cd AIPriceOptimization
.\gradlew.bat :app:assembleDevDebug
```

The `dev` build flavor targets `http://10.0.2.2:8000/` (the Android Emulator's
alias for your machine's `localhost`) so you can point it at a locally
running FastAPI instance. See `docs/Release_Runbook.md` for all build
variants and environment configuration.

### Running tests

```powershell
.\gradlew.bat :app:testDevDebugUnitTest
```

## Project structure

```
app/src/main/java/com/tyejaedon/aipriceoptimization/
  core/        common types, error model, dispatchers, navigation
  data/        remote (Retrofit/DTOs), repository implementations
  domain/      models, repository interfaces, use cases
  feature/     one package per screen/flow (splash, dashboard, ...)
  di/          Hilt modules
```

## Non-negotiables

- The backend response is always authoritative; client-side validation is
  for usability only.
- No model artifacts (`.joblib`, TF-IDF/SVD/KD-Tree files) are ever loaded
  on-device.
- No Firebase service-account keys or `google-services.json` are committed.
- Every code change maps to a GitHub issue — see `CONTRIBUTING.md`.

## Documentation

| Doc                               | Purpose                                           |
|-----------------------------------|---------------------------------------------------|
| `docs/Mobile_Blueprint.md`        | Architecture, screens, navigation, testing        |
| `docs/API_Contract.md`            | Backend request/response contract                 |
| `docs/Firestore_Client_Access.md` | Firestore ownership & security rules direction    |
| `docs/Release_Runbook.md`         | Build variants, Firebase setup, release checklist |
| `.github/copilot-instructions.md` | Coding-agent operating rules for this repo        |

## Repository governance

- `main` is the default branch and is protected: pull requests are
  required, `Android CI / build-and-test` must pass, linear history is
  enforced (squash/rebase merges only), and force pushes/deletions are
  blocked — including for administrators. The live rule is mirrored in
  [`.github/branch-protection.json`](.github/branch-protection.json).
- Secret scanning and push protection, and Dependabot security updates are
  enabled at the repository level.
- Work is tracked with milestones `Phase 1` through `Phase 7` (see
  `docs/Mobile_Blueprint.md`, section 9) and `phase-*` issue labels.
- See `CONTRIBUTING.md` for the branch-naming and PR conventions every
  change must follow.

