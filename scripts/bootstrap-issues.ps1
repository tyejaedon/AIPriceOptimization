param()

$repo = "tyejaedon/AIPriceOptimization"

$issues = @(
  @{
    title = "Bootstrap Android Compose client and repository tooling"
    milestone = "Phase 1 - Bootstrap"
    labels = "phase-1-bootstrap,android,ci"
    body = @"
## Goal
Establish the Kotlin/Jetpack Compose client shell for the existing server-side
pricing platform, plus the repository tooling required by the issue-first workflow.

## Scope
- Android Studio project with namespace ``com.tyejaedon.aipriceoptimization``.
- Kotlin + Compose + Material 3 + Hilt + Retrofit/kotlinx.serialization + Room + DataStore wiring.
- ``dev`` / ``staging`` / ``production`` product flavors with environment-specific ``API_BASE_URL``.
- Navigation shell (``NavRoutes`` + ``AppNavHost``) with Splash and Dashboard.
- ``GET /health`` through ``HealthRepository`` with ``AppResult`` / ``AppError`` mapping.
- Docs: ``README.md``, ``docs/Mobile_Blueprint.md``, ``docs/API_Contract.md``,
  ``docs/Firestore_Client_Access.md``, ``docs/Release_Runbook.md``, ``CONTRIBUTING.md``.
- CI workflow, issue templates, PR template, copilot instructions.
- ``.gitignore`` / ``.gitattributes`` excluding secrets and IDE state.

## Acceptance criteria
- [x] Debug build configuration resolves in Android Studio.
- [x] Compose splash and dashboard render.
- [x] Navigation shell exists.
- [x] API base URL is environment-configurable per flavor.
- [x] ``GET /health`` is reachable through a repository abstraction.
- [x] No service-account credentials, keystores, or ``google-services.json`` committed.
- [x] Unit tests exist for repository, use case, and ViewModels.
- [x] README documents local setup.

## Notes
No pricing logic and no Firebase Authentication in this phase.
"@
  },
  @{
    title = "CI: verify Android pipeline on main and enable required status check"
    milestone = "Phase 1 - Bootstrap"
    labels = "phase-1-bootstrap,ci"
    body = @"
## Goal
Make ``Android CI`` reliably green on ``main`` and then register ``build-and-test``
as a required status check on the protected ``main`` branch.

## Scope
- Confirm the runner JDK matches ``gradle/gradle-daemon-jvm.properties`` (toolchain 25).
- Confirm ``compileSdk``/AGP versions resolve on ``ubuntu-latest``.
- Confirm ``:app:lintDevDebug``, ``:app:testDevDebugUnitTest``, ``:app:assembleDevDebug`` pass.
- Add the ``build-and-test`` context to the ``main`` branch protection rule.

## Acceptance criteria
- [ ] ``Android CI`` passes on ``main``.
- [ ] ``build-and-test`` is a required status check on ``main``.
- [ ] ``docs/Release_Runbook.md`` documents the CI commands and JDK requirement.
"@
  },
  @{
    title = "Firebase Authentication: sign in, register, forgot password"
    milestone = "Phase 2 - Authentication"
    labels = "phase-2-auth,android,security"
    body = @"
## Goal
Add Firebase Authentication as the identity provider and branch the splash
route on authentication state.

## Scope
- ``feature/auth`` screens: sign in, register, forgot password.
- ``AuthRepository`` + ``AuthRepositoryImpl`` behind a ``di/`` binding.
- Session restoration and sign-out clearing user-scoped local cache.
- Splash routes to Dashboard when authenticated, Sign In otherwise.

## Acceptance criteria
- [ ] Email/password sign in, registration, and password reset work.
- [ ] Loading, error, and validation states are explicit in the UI state.
- [ ] User input preserved on failure.
- [ ] No tokens, emails, or passwords logged.
- [ ] Unit tests for auth state transitions; Compose tests for form validation.
- [ ] ``google-services.json`` remains untracked and documented in README.
"@
  },
  @{
    title = "Authenticated API access: Firebase ID token provider and OkHttp interceptor"
    milestone = "Phase 2 - Authentication"
    labels = "phase-2-auth,android,security,api-contract"
    body = @"
## Goal
Attach Firebase ID tokens to backend requests without blocking networking threads.

## Scope
- ``TokenProvider`` abstraction returning a fresh ID token.
- OkHttp ``Authorization: Bearer <token>`` interceptor with refresh handling.
- ``401`` handling: refresh once, then route to sign-in.
- Authorization header redaction in logs; ``HttpLoggingInterceptor`` stays BASIC/NONE.

## Acceptance criteria
- [ ] Authenticated requests carry the bearer token.
- [ ] Token acquisition failures map to ``AppError`` and never crash.
- [ ] No token value is ever logged.
- [ ] Unit tests cover token attach, refresh, and failure paths.
"@
  },
  @{
    title = "Mentor profile and country onboarding"
    milestone = "Phase 3 - Profile"
    labels = "phase-3-profile,android,firestore"
    body = @"
## Goal
Collect and persist the mentor profile required by the pricing request.

## Scope
- ``feature/profile`` and onboarding profile/country screens.
- ``UserProfile`` domain model and ``ProfileRepository``.
- Firestore access restricted to ``mentors/{mentorId}`` via ``data/firestore``.
- Country selector component with ISO-2 validation.

## Acceptance criteria
- [ ] Profile read/write only through repository boundaries.
- [ ] Loading, empty, offline, and error states handled.
- [ ] Never trusts a client-supplied ``mentor_id``; uses the authenticated UID.
- [ ] Unit tests for mappers and ViewModel; Compose tests for validation.
"@
  },
  @{
    title = "Firestore security rules and emulator test suite"
    milestone = "Phase 3 - Profile"
    labels = "phase-3-profile,firestore,security,testing"
    body = @"
## Goal
Enforce and verify user-scoped Firestore access per ``docs/Firestore_Client_Access.md``.

## Scope
- Finalize ``firebase/firestore.rules`` for ``mentors/{mentorId}`` and subcollections.
- Deny client writes to ``historical_transactions`` and reference collections.
- Firebase Emulator Suite test project and npm scripts.

## Acceptance criteria
- [ ] A user can read/update only their own profile.
- [ ] Cross-user profile access is denied.
- [ ] Clients cannot write official historical transactions.
- [ ] Reference data is read-only for authenticated clients.
- [ ] Unauthenticated access is denied.
- [ ] Emulator tests run locally and are documented.
"@
  },
  @{
    title = "Pricing input form with client-side validation"
    milestone = "Phase 4 - Pricing workflow"
    labels = "phase-4-pricing,android"
    body = @"
## Goal
Implement the pricing request form described in ``docs/Mobile_Blueprint.md``.

## Scope
- Fields: raw description, industry, mentor country, client country,
  competitiveness score (0.0-1.0), market saturation score (0.0-1.0).
- ``PricingFormState`` holder with ``validationErrors``, ``isSubmitting``, ``errorMessage``.
- Reusable components: ``DescriptionInput``, ``IndustrySelector``, ``CountrySelector``, ``ScoreSlider``.

## Acceptance criteria
- [ ] Submit is disabled while the form is invalid or submitting.
- [ ] Validation is usability-only; the server remains authoritative.
- [ ] Raw description is never logged.
- [ ] Unit tests for validation; Compose tests for the disabled/enabled submit state.
"@
  },
  @{
    title = "Pricing API integration and error mapping"
    milestone = "Phase 4 - Pricing workflow"
    labels = "phase-4-pricing,api-contract,android"
    body = @"
## Goal
Integrate ``POST /api/v1/optimize-price`` behind a repository with stable error mapping.

## Scope
- Request/response DTOs per ``docs/API_Contract.md``.
- ``PricingRepository`` + ``OptimizePriceUseCase`` returning ``AppResult``.
- HTTP status mapping: 401, 403, 422, 429, 500, 503, timeout, offline.
- Draft preservation on failure and retry without data loss.

## Acceptance criteria
- [ ] All documented status codes map to a distinct ``AppError``.
- [ ] Form data is preserved on every failure path.
- [ ] Malformed responses and empty peer lists are handled.
- [ ] MockWebServer contract tests cover success and each failure case.
"@
  },
  @{
    title = "Recommendation result screen and peer explainability"
    milestone = "Phase 5 - Results"
    labels = "phase-5-results,android,accessibility"
    body = @"
## Goal
Present the authoritative backend recommendation with an explicit fee breakdown.

## Scope
- Display separately: base predicted rate, M-Pesa surcharge, final quoted rate, currency.
- Show bilateral arbitrage factor, industry, mentor/client country, neighbor count, timestamp.
- Peer matches with similarity as a percentage and verified rate as currency.
- Plain-language explanation and a "recommendation, not a guarantee" disclaimer.

## Acceptance criteria
- [ ] The surcharge is never folded into the final number.
- [ ] ``peer_index`` is never presented as a personal identifier.
- [ ] Every money value carries an explicit currency.
- [ ] Navigation passes an identifier, not the recommendation object.
- [ ] Compose tests cover the breakdown and peer rendering; accessibility semantics verified.
"@
  },
  @{
    title = "Quote history, Room drafts, and offline states"
    milestone = "Phase 6 - History and offline"
    labels = "phase-6-history,android"
    body = @"
## Goal
Add local persistence for drafts and a read-only quote history projection.

## Scope
- Room entities/DAO for draft requests and cached quote history.
- ``history`` list and detail screens with stale-data indicators.
- Offline banner and retry queue; DataStore for preferences.

## Acceptance criteria
- [ ] Drafts survive process death and navigation away from the form.
- [ ] Cached results are always labelled with a generation timestamp.
- [ ] Cached prices are never presented as live quotes.
- [ ] The client never writes official historical transactions.
- [ ] Unit tests for DAO and draft logic; Compose tests for offline behavior.
"@
  },
  @{
    title = "Hardening: contract tests, accessibility, crash reporting, release signing"
    milestone = "Phase 7 - Hardening"
    labels = "phase-7-hardening,testing,security,accessibility"
    body = @"
## Goal
Prepare the client for a release build.

## Scope
- API contract test matrix, Firestore emulator suite in CI, Compose UI test pass.
- Accessibility audit: 48dp targets, content descriptions, font scaling, contrast.
- Crash reporting with payload redaction.
- Release signing configuration sourced from CI secrets, never committed.
- R8/minification and resource shrinking for release builds.

## Acceptance criteria
- [ ] CI runs unit, Compose, and emulator tests.
- [ ] No sensitive values are logged in any build type.
- [ ] Release build is signed from environment-provided material only.
- [ ] ``docs/Release_Runbook.md`` documents the full release procedure.
"@
  }
)

foreach ($i in $issues) {
    $tmp = New-TemporaryFile
    Set-Content -Path $tmp -Value $i.body -Encoding UTF8
    gh issue create --repo $repo --title $i.title --body-file $tmp --label $i.labels --milestone $i.milestone
    Remove-Item $tmp
}

