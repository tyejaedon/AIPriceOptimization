# Contributing

## Issue-first workflow

Every code change must map to an existing GitHub issue. If you don't have an
issue number, open one (or ask for one) before creating a branch.

## Branch naming

```
<type>/<issue-number>-<short-slug>
```

Allowed types: `feature`, `fix`, `chore`, `docs`, `refactor`, `test`.

Example: `feature/1-android-client-bootstrap`

Never commit directly to `main`.

## Branch protection on `main`

`main` is protected. The live rule is mirrored in
[`.github/branch-protection.json`](.github/branch-protection.json) so it can be
reviewed and re-applied:

- Pull request required before merging (stale reviews are dismissed).
- Linear history required (squash or rebase merges only; merge commits are
  disabled at the repository level).
- Force pushes and branch deletion are blocked.
- Conversation resolution required.
- Rules are enforced for administrators as well.
- Status checks must be up to date with `main` before merging; the
  `build-and-test` context from `Android CI` is the required check.

Re-apply the rule with:

```powershell
gh api -X PUT repos/<owner>/<repo>/branches/main/protection `
  --input .github/branch-protection.json
```

## Pull requests

- Open as a **draft** first.
- Must contain `Closes #<issue-number>`.
- Must declare the target milestone.
- Must include the validation commands you ran and their results (e.g.
  `./gradlew.bat :app:testDevDebugUnitTest` output summary).

## Before implementing a feature

Read:

- `README.md`
- `docs/Mobile_Blueprint.md`
- `docs/API_Contract.md`
- `docs/Firestore_Client_Access.md`
- `.github/copilot-instructions.md`

## Definition of done

A feature is complete only when:

- It follows the existing package boundaries (`core`/`data`/`domain`/
  `feature`/`di`).
- Loading, success, empty, offline, and error UI states are all handled
  where applicable.
- User input is preserved across failures (never silently discard a form).
- No sensitive values (tokens, raw descriptions, emails) are logged.
- Unit tests pass; Compose tests pass where applicable.
- Documentation (`docs/*.md`) is updated to reflect the change.
- No secrets, keystores, or `google-services.json` are committed.

## Data hygiene

Do not commit generated datasets, local emulator data dumps, or build logs.
`.gitignore` already excludes the common cases — extend it rather than
working around it.

