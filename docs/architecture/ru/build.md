# Сборка

[English version](../en/build.md) · [Все разделы](README.md)

Общая настройка модулей лежит в [`build-logic/`](../../../build-logic/), включённой сборке (included build) с convention-плагинами. Модуль подключает один-два плагина и объявляет только свои зависимости.

&nbsp;

## Convention-плагины

Каждому виду модуля — свой плагин:

| Плагин | Для чего | Что добавляет |
|---|---|---|
| `shoppingapp.android.application` | `:apps:shop`, `:apps:uikit` | настройку Android, targetSdk, подпись, R8 в release, [проверку графа модулей](modules.md#проверка) |
| `shoppingapp.android.library` | каждая Android-библиотека | compileSdk, minSdk, Java |
| `shoppingapp.android.compose` | модули с Compose | компилятор Compose, BOM, Material 3 |
| `shoppingapp.android.hilt` | модули с Hilt | Hilt с KSP |
| `shoppingapp.android.feature` | каждый `:feature:<name>:impl` | library + Compose + Hilt + serialization, библиотеки навигации и lifecycle |
| `shoppingapp.jvm.library` | `:shared:domain`, `:shared:analytics` | Kotlin JVM без Android |

Поэтому build-файл модуля короткий:

```kotlin
// feature/promo/ui/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.promo.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
}
```

Версии SDK и Java задаются в одном месте, в [`AndroidConfig.kt`](../../../build-logic/src/main/kotlin/AndroidConfig.kt):

```kotlin
internal object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 26
    const val TARGET_SDK = 36
    val JAVA_VERSION = JavaVersion.VERSION_21
}
```

&nbsp;

## Версии и репозитории

Все версии — в [`gradle/libs.versions.toml`](../../../gradle/libs.versions.toml), и build-logic читает тот же каталог. Модули подключают плагины без версий.

- **Репозитории объявлены только в `settings.gradle.kts`**: модуль, объявивший свои, роняет сборку. Из Google Maven берутся только артефакты Google и AndroidX.
- **Включены configuration cache и параллельная сборка.**
- **Сервер — отдельная сборка** со своим wrapper и каталогом, в настройки приложения она не входит.

&nbsp;

## Стиль кода

Стиль проверяет [ktlint](https://pinterest.github.io/ktlint/) 1.8 через Spotless, правила лежат в корневом [`.editorconfig`](../../../.editorconfig):

```sh
./gradlew spotlessCheck   # проверить
./gradlew spotlessApply   # исправить
```

- **Стиль `android_studio`** — тот же, что у форматера Android Studio, поэтому Ctrl+Alt+L и ktlint не спорят.
- **120 символов в строке.**
- **Запятая после последнего элемента** в многострочных списках: diff в одну строку, и строки можно переставлять.
- **По параметру на строку, если их два и больше.**

Комментарии — на английском, короткие, и объясняют то, чего не скажет код: причину, важный порядок, обходное решение. У очевидного кода комментариев нет.

&nbsp;

## CI

[`ci.yml`](../../../.github/workflows/ci.yml) запускается на каждый pull request и каждый push в `main`. Две задачи идут параллельно:

| Задача | Шаги |
|---|---|
| Android | стиль кода → правила модулей (`assertModuleGraph`) → lint → сборка обоих приложений |
| Server | стиль кода и тесты, включая проверку `data/*.json` |

Новый push в pull request отменяет его ещё не законченный прогон, а прогоны на `main` всегда доходят до конца:

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: ${{ github.event_name == 'pull_request' }}
```

&nbsp;

## Подпись

Debug-ключ лежит в репозитории ([`build-logic/debug.keystore`](../../../build-logic/debug.keystore)), поэтому у сборки с любого компьютера одна и та же подпись. От неё зависят App Links: отпечаток ключа указан в `assetlinks.json`.

- **Release подписан тем же ключом**, чтобы ставиться на эмулятор; для публикации в магазине нужен настоящий ключ.
- **В release включён R8**: он сжимает код и ресурсы.

&nbsp;

## Команды

Основные команды из корня проекта:

```sh
./gradlew :apps:shop:installDebug      # магазин на подключённое устройство
./gradlew :apps:uikit:installDebug     # приложение UI kit
./gradlew assertModuleGraph            # зависимости между модулями
./gradlew lintDebug                    # Android lint во всех модулях
cd server && ./gradlew test            # тесты сервера
```
