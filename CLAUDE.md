# ShoppingApp

Android app (Kotlin, Jetpack Compose, Hilt, Room, KSP). Gradle 9.8, AGP 9.4, JDK 21, compileSdk 37.
applicationId: `krio.systemdesign.shoppingapp`, launcher activity: `.MainActivity`.

## Modules
- `:app` — entry point, navigation host
- `:app-uikit` — separate app showing every `:core:ui` component (UI kit catalog); applicationId `krio.systemdesign.shoppingapp.uikit`, launcher activity `.UiKitActivity`. Depends only on `:core:ui`: a new reusable component goes into `:core:ui` and gets a sample in `app-uikit/.../sections/`.
  Packages in `core/ui/.../components/` mirror the UI kit sections one to one (`buttons` ↔ `ButtonsSection`, `cards` ↔ `CardsSection`, ...): a component goes into the package of the section that shows it.
- `:domain`, `:data`
- `:core:ui` (shared Compose components and theme), `:core:network`, `:core:config`
  Icons are Material Symbols as `ImageVector` in `core/ui/.../icons/symbols/` (`MaterialSymbols.Home`…), there is no material-icons dependency: a new icon is an SVG from fonts.google.com/icons converted with the Valkyrie plugin.
- `:feature:catalog`, `:feature:cart`, `:feature:promo`, `:feature:checkout`, `:feature:settings`
- `build-logic/` (included build, not an app module) — convention plugins `shoppingapp.android.{library,application,compose,hilt,feature}`. compileSdk/minSdk/targetSdk/Java live in `build-logic/src/main/kotlin/AndroidConfig.kt`; a new feature module applies `libs.plugins.shoppingapp.android.feature` and declares only its own dependencies.
- `server/` — Ktor backend for the app (Kotlin 2.3, Ktor 3.6, data in JSON files). A separate Gradle build with its own wrapper and version catalog, not included in `settings.gradle.kts`. API, data rules and deploy: `server/README.md`.

## Build
- `./gradlew :app:assembleDebug` — build debug APK (`app/build/outputs/apk/debug/app-debug.apk`)
- `./gradlew :app:installDebug` — build and install on the connected device
- `./gradlew :app-uikit:installDebug` — build and install the UI kit catalog; in Android Studio it is the `app-uikit` run configuration
- `cd server && ./gradlew test` — server tests; `server/deploy.sh` — test, build and restart the deployed server
- Never run two Gradle builds in the same checkout at once: they share `build/` dirs and corrupt each other's outputs.

Machine-specific setup (SDK paths, emulator access) lives in `CLAUDE.local.md`, if present.
