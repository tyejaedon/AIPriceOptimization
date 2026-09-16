# Release Runbook

## Toolchain

- Android Studio: latest stable channel compatible with AGP `9.4.0`.
- JDK: 11 (see `compileOptions` in `app/build.gradle.kts`); Gradle daemon
  itself runs on the JDK resolved by the Foojay toolchain resolver plugin.
- Kotlin: `2.2.10`
- Gradle: wrapper-managed, see `gradle/wrapper/gradle-wrapper.properties`.
- Compose BOM: `2026.02.01`

> Dependency versions in `gradle/libs.versions.toml` were verified against
> live Maven Central / Google Maven metadata (not guessed) as of this
> writing: Hilt `2.60.1`, KSP `2.2.10-2.0.2` (must always match the Kotlin
> version exactly), Room `2.8.5`, Navigation Compose `2.10.1`, Hilt
> Navigation Compose `1.4.0`, DataStore `1.2.1`, Retrofit `2.12.0` (pinned to
> the 2.x line — see note below), OkHttp `4.12.0`, kotlinx-serialization-json
> `1.11.0`, kotlinx-coroutines `1.11.0`.
>
> **Known AGP 9.x incompatibility:** Hilt's Gradle plugin versions up to
> `2.56.2` fail with `IllegalStateException: Android BaseExtension not
> found` under AGP `9.4.0`, because AGP removed the long-deprecated
> `com.android.build.gradle.BaseExtension` class that older Hilt plugin
> versions relied on. This is fixed by using Hilt `2.60.1`+ (already applied
> in the version catalog). If you ever see this error again after bumping
> AGP further, check the Hilt release notes for a newer plugin version first.
>
> **Why Retrofit is pinned to 2.x:** Retrofit `3.0.0` is available, but
> `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter` has
> not published a release since 2023 (still at `1.0.0`) and its
> compatibility with Retrofit 3 is unverified. Revisit this once either the
> converter is updated for Retrofit 3, or it's replaced with
> `kotlinx-serialization`'s own `Converter.Factory` shipped by a
> maintained library.

## Build variants (flavor dimension: `environment`)

```
dev         -> http://10.0.2.2:8000/        (emulator loopback, cleartext allowed)
staging     -> https://staging-api.example.com/
production  -> https://api.example.com/
```

Update the flavor `buildConfigField("String", "API_BASE_URL", ...)` values in
`app/build.gradle.kts` per environment. Read the value in code via
`BuildConfig.API_BASE_URL` (see `data/remote/NetworkModule.kt`).

For local backend development, remember: use `10.0.2.2`, not `localhost`,
from inside the Android Emulator.

## First local build

```powershell
.\gradlew.bat :app:assembleDevDebug
.\gradlew.bat :app:testDevDebugUnitTest
```

## Firebase setup (Phase 2+)

1. Create/select a Firebase project per environment (dev/staging/prod).
2. Download `google-services.json` for the matching Firebase Android app and
   place it at `app/google-services.json`. This file is git-ignored; it must
   be supplied per environment/developer, never committed.
3. Apply the Google Services Gradle plugin and Firebase BOM once Phase 2
   (Authentication) begins — do not add them speculatively before that
   work starts, to keep the build green without the JSON file.
4. Use the Firebase Emulator Suite for local Firestore rules testing:
   ```powershell
   firebase emulators:start --only auth,firestore
   ```

## Testing

```powershell
.\gradlew.bat :app:testDevDebugUnitTest
.\gradlew.bat :app:connectedDevDebugAndroidTest
```

## Release checklist

- [ ] All unit tests pass.
- [ ] All Compose UI tests pass.
- [ ] Firestore emulator rule tests pass (once rules are deployed).
- [ ] No secrets, service-account keys, or `google-services.json` committed.
- [ ] `production` flavor points at the real production API base URL.
- [ ] Release build type has minification/resource shrinking configured
      before shipping (`optimization.enable` is currently `false` for the
      bootstrap phase — revisit before a real release).
- [ ] Crash reporting configured.
- [ ] Signing config supplied via environment/CI secrets, not committed.

