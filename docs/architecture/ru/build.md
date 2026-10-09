# Сборка

[English version](../en/build.md) · [Все разделы](README.md)

Общая настройка модулей лежит в [`build-logic/`](../../../build-logic/), включённой сборке (included build) с convention-плагинами. Модуль подключает несколько из них и объявляет только свои зависимости.

&nbsp;

## Convention-плагины

Каждому виду модуля — свой плагин:

| Плагин | Для чего | Что добавляет |
|---|---|---|
| `shoppingapp.android.application` | `:apps:shop`, `:apps:uikit` | настройку Android, targetSdk, подпись, R8 в release, [проверку графа модулей](modules.md#проверка), анализ зависимостей, предупреждения как ошибки в CI |
| `shoppingapp.android.library` | каждая Android-библиотека | compileSdk, minSdk, Java, анализ зависимостей, предупреждения как ошибки в CI, настройку тестов: unit-тесты только на debug, [Robolectric](testing.md#база-данных), [образцы API](testing.md#контракт-с-сервером), сообщение упавшего теста в логе |
| `shoppingapp.android.compose` | модули с Compose | компилятор Compose, BOM, Material 3, правила lint для Compose от Slack |
| `shoppingapp.android.hilt` | модули с Hilt | Hilt с KSP |
| `shoppingapp.android.feature` | каждый `:feature:<name>:impl` | library + Compose + Hilt + serialization, библиотеки навигации и lifecycle |
| `shoppingapp.android.screenshots` | каждая библиотека с превью | [скриншот-тесты](testing.md#скриншоты) из превью |
| `shoppingapp.jvm.library` | `:shared:domain`, `:shared:analytics`, `:core:config` | Kotlin JVM без Android, анализ зависимостей, предупреждения как ошибки в CI, сообщение упавшего теста в логе |

Поэтому build-файл модуля короткий:

```kotlin
// feature/promo/ui/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
    alias(libs.plugins.shoppingapp.android.screenshots)
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
    const val TARGET_SDK = 37
    val JAVA_VERSION = JavaVersion.VERSION_21
}
```

&nbsp;

## Версии и репозитории

Все версии — в [`gradle/libs.versions.toml`](../../../gradle/libs.versions.toml), и build-logic читает тот же каталог. Модули подключают плагины без версий.

- **Репозитории объявлены только в `settings.gradle.kts`**: модуль, объявивший свои, роняет сборку. Из Google Maven берутся только артефакты Google и AndroidX.
- **Включены configuration cache, build cache и параллельная сборка.** Build cache хранит результаты задач, и CI тоже берёт их из прошлых прогонов.
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

[`ci.yml`](../../../.github/workflows/ci.yml) запускается на каждый pull request и каждый push в `main` и ветки вариантов (`kmp-ready`, `kmp`), кроме изменений только в документации, README и их картинках. Две задачи идут параллельно:

| Задача | Шаги |
|---|---|
| Android | стиль кода (ktlint) → зависимости между модулями (`assertModuleGraph`) → неиспользуемые зависимости (`buildHealth`) → ошибки в коде и ресурсах (Android lint с правилами Compose от Slack) → [unit- и скриншот-тесты](testing.md) → сборка обоих приложений |
| Server | стиль кода (ktlint) → API и файлы данных (тесты, включая проверку `data/*.json` и [образцов API](testing.md#контракт-с-сервером)) |

- **Каждая проверка запускается, даже если предыдущая упала**, так что один прогон показывает все проблемы.
- **Предупреждения в CI — ошибки**, и у Kotlin, и у lint: CI передаёт `-PwarningsAsErrors=true`, его читают convention-плагины ([`WarningsAsErrors.kt`](../../../build-logic/src/main/kotlin/WarningsAsErrors.kt)) и сборка сервера. Локальная сборка их только печатает.
- **Неиспользуемые зависимости** находит плагин [Dependency Analysis](https://github.com/autonomousapps/dependency-analysis-gradle-plugin); его правила — какие советы он игнорирует и почему — в корневом [`build.gradle.kts`](../../../build.gradle.kts).
- **Исключения lint** — в корневом [`lint.xml`](../../../lint.xml): CompositionLocal, которые проект создаёт намеренно, и «вышла новая версия» — это только подсказка, чтобы релиз какой-нибудь библиотеки не ронял CI.
- **Упавшие тесты видны там, куда и так смотришь**: каждый, с сообщением и строкой, — вверху страницы прогона, у строки теста на вкладке Files changed в PR, если файл теста входит в изменения PR, а ниже — сводка с полным стеком ([action-junit-report](https://github.com/mikepenz/action-junit-report), единственное действие в CI не от GitHub и не от Gradle).
- **Android для Robolectric кэшируется** между прогонами: Robolectric скачивает его в `~/.m2`, который кэш Gradle не покрывает.
- **Задача останавливается через 30 минут** (серверная — через 15), а не висит 6 часов, как по умолчанию в GitHub.

Новый push в pull request отменяет его ещё не законченный прогон, а прогоны на ветках всегда доходят до конца:

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: ${{ github.event_name == 'pull_request' }}
```

[`latest-build.yml`](../../../.github/workflows/latest-build.yml) после успешного CI на push в `main`, `kmp-ready` или `kmp` собирает release-APK приложения и UI kit и публикует их как релиз `<ветка>-latest`. Ссылки на скачивание в README ведут на него. Сборка `main` — главный релиз репозитория, остальные ветки — pre-release.

[`dependency-graph.yml`](../../../.github/workflows/dependency-graph.yml) после каждого push в `main` отправляет в GitHub список библиотек, с которыми выходят приложение и сервер. GitHub проверяет их по базе известных уязвимостей и, если найдёт, показывает Dependabot alert во вкладке Security. Это отдельный workflow, потому что для отправки нужно право записи, а CI остаётся только на чтение.

[`screenshot-comment.yml`](../../../.github/workflows/screenshot-comment.yml) после CI, упавшего на скриншотах, показывает изменившиеся скриншоты в комментарии к pull request. Комментарий показывает только картинку со ссылкой, поэтому картинки кладутся в ветку `screenshots/pr-<N>`, которая удаляется, когда скриншоты снова совпали или pull request закрыт. Комментарий у pull request один и обновляется при каждом прогоне. Ему тоже нужно право записи, и код pull request он никогда не запускает: только копирует картинки, сделанные CI.

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
./gradlew buildHealth                  # неиспользуемые зависимости
./gradlew lintDebug                    # Android lint во всех модулях
./gradlew test                         # тесты приложения, вместе со скриншотами
./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true  # новые скриншоты после изменения UI
cd server && ./gradlew test            # тесты сервера
```

Те же проверки, что в CI, с предупреждениями как ошибками:

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug test :apps:shop:assembleDebug :apps:uikit:assembleDebug
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```
