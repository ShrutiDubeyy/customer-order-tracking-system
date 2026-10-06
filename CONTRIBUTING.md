# Contributing and Branch Policy

## Branches
| Branch | Purpose | Rules |
|--------|---------|-------|
| `main` | Stable, release-ready code. Every release is tagged (v0.1.0, v0.2.0 ...). | No direct commits. Updated only by merging `develop` or a `hotfix/*` branch through a pull request. |
| `develop` | Integration branch for finished features. | No direct commits. Updated only by pull requests from `feature/*` and `bugfix/*`. Jenkins builds this branch. |
| `feature/<issue-no>-<short-name>` | One new feature or task. | Branch from `develop`. Example: `feature/12-item-catalogue`. |
| `bugfix/<issue-no>-<short-name>` | Fix for a defect found in `develop`. | Branch from `develop`. Example: `bugfix/21-negative-stock`. |
| `hotfix/<short-name>` | Urgent fix for `main`. | Branch from `main`; merge into `main` and `develop`. |

Naming rules: lowercase letters, digits and hyphens only; start with the type prefix; include the issue number; keep the name short.

## Commit messages
Format: `<type>: <short summary in present tense>` (max about 72 characters).

| Type | Use for |
|------|---------|
| `feat` | new feature |
| `fix` | bug fix |
| `docs` | documentation only |
| `test` | adding or changing tests |
| `build` | Maven, Docker, Jenkinsfile, pipeline changes |
| `chore` | housekeeping (ignore files, templates, configuration) |
| `refactor` | code change that does not change behaviour |

Examples: `feat: add item catalogue page`, `fix: reject orders above available stock`.
Commit small and often; one logical change per commit.

## Pull requests
1. Push your branch and open a pull request into `develop` (use the PR template).
2. Link the issue (`Closes #12`).
3. The Jenkins build and tests must pass.
4. At least one review comment or approval before merging.
5. Merge, then delete the feature branch.

## Definition of Done
A change is done when it is merged through a reviewed pull request, the build and tests pass,
acceptance criteria are met, documentation is updated and the backlog is updated.
