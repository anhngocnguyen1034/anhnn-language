# Repository Guidelines

## Project Structure & Module Organization

This repository is a single-module Android library for a reusable Jetpack Compose language picker. Kotlin source lives in `src/main/java/com/anhnn/language/`:

- `LanguageManager.kt` defines supported languages, flag resource mapping, and locale application.
- `LanguageDataSource.kt` persists the selected language with DataStore Preferences.
- `LanguageScreen.kt` provides the Compose UI and portrait-lock behavior.

Resources live in `src/main/res/`. Localized module strings are in `values-*` folders, and vector flag drawables are in `drawable/`. Root Gradle files (`build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`) define the Android library, publishing, and toolchain.

## Build, Test, and Development Commands

- `./gradlew assembleDebug` builds the debug AAR and validates compilation quickly.
- `./gradlew assembleRelease` builds the release AAR used by publication.
- `./gradlew publishToMavenLocal` publishes the release artifact to the local Maven repository.
- `./gradlew clean` removes Gradle build outputs.

Use the Gradle wrapper, not a system Gradle install.

## Coding Style & Naming Conventions

Use Kotlin with 4-space indentation and keep public APIs in package `com.anhnn.language`. Prefer concise Compose functions with state held via `remember`, `LaunchedEffect`, and coroutine scopes following existing patterns. Resource names must be lowercase snake case; locale folders should follow Android qualifiers such as `values-vi` or `values-zh-rCN`. When a resource name conflicts with Kotlin keywords, reference it with backticks, for example `R.drawable.\`in\``.

## Testing Guidelines

There are currently no committed tests. For behavior changes, at minimum run `./gradlew assembleDebug` and `./gradlew assembleRelease`. If tests are added, place JVM tests under `src/test/` and Android or Compose instrumentation tests under `src/androidTest/`, using names like `LanguageManagerTest` or `LanguageScreenTest`.

## Commit & Pull Request Guidelines

Recent commits use short, descriptive messages such as `docs: add integration guide` and `initial release: anhnn language module v1.0.0`. Keep commits focused and use a clear prefix when helpful (`docs:`, `fix:`, `feat:`).

Pull requests should include a summary of the change, validation commands run, and screenshots or recordings for UI changes. For new languages, update `LanguageManager.Language`, add the flag vector, add the localized `anhnn_select_language` string, and update the README language table.

## Agent-Specific Instructions

Do not remove existing locales or generated resources unless explicitly requested. Keep edits scoped to the library module, and preserve the public integration contract documented in `README.md`.
