# Сборка

[English version](../en/build.md) · [Все разделы](README.md)

Общая настройка модулей вынесена в [`build-logic/`](../../../build-logic/) — подключаемую сборку (included build) с convention-плагинами. Каждый модуль применяет несколько таких плагинов и объявляет только собственные зависимости.

&nbsp;

## Convention-плагины

Для каждого вида модулей есть свой плагин:

| Плагин | Для чего | Что добавляет |
|---|---|---|
| `shoppingapp.android.application` | `:apps:shop:android`, `:apps:uikit` | настройку Android, targetSdk, подпись, R8 в release, языки приложения для системных настроек (список собирается из ресурсов; у магазина строки в ресурсах Compose, и он перечисляет языки сам, в `res/xml/locale_config.xml`), [проверку графа модулей](modules.md#проверка), анализ зависимостей, предупреждения как ошибки в CI |
| `shoppingapp.android.compose` | `:apps:shop:android`, `:apps:uikit` | компилятор Compose, BOM, Material 3, правила lint для Compose от Slack |
| `shoppingapp.metro` | модули с DI, Android и KMP | Metro: `internal`-класс с `@Contributes*` остаётся `internal`, а `internal` binding container роняет сборку |
| `shoppingapp.android.screenshots` | каждая библиотека с превью | [скриншот-тесты](testing.md#скриншоты), созданные из превью |
| `shoppingapp.kmp.library` | каждая библиотека на Kotlin Multiplatform: `:core:*`, `:crossfeature:*`, `:feature:<name>:ui` и модули `testing` | таргеты Android и iOS, настройку Android и [Robolectric](testing.md#база-данных) для host-тестов, Android lint и для общего кода, анализ зависимостей, предупреждения как ошибки в CI, текст ошибки упавшего теста в логе |
| `shoppingapp.kmp.compose` | KMP-модули с Compose: `:core:designsystem`, `:core:compose-utils`, `:crossfeature:ui`, `:feature:<name>:ui`, общий `:apps:shop:app` магазина | Compose Multiplatform, его основные библиотеки и ресурсы (`Res`), правила lint для Compose от Slack |
| `shoppingapp.kmp.feature` | каждый `:feature:<name>:impl` | `kmp.library` + `kmp.compose` + Metro + serialization, библиотеки навигации и lifecycle от JetBrains для KMP |

Поэтому build-файл модуля получается коротким:

```kotlin
// feature/promo/ui/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.kmp.compose)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:designsystem"))
        }
    }
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

Все версии указаны в [`gradle/libs.versions.toml`](../../../gradle/libs.versions.toml), и build-logic читает тот же каталог. Модули подключают плагины без указания версий.

- **Репозитории объявлены только в `settings.gradle.kts`**: если модуль объявит свои, сборка упадёт. Из Google Maven берутся только артефакты Google и AndroidX.
- **Включены configuration cache, build cache и параллельная сборка.** Build cache хранит результаты задач, и CI тоже переиспользует их из прошлых прогонов.
- **Сервер — это отдельная сборка** со своим wrapper и каталогом версий, в настройки приложения она не входит.
- **Контракт (`contract/`) — тоже отдельная сборка**, её подключают и приложение, и сервер (`includeBuild`): это DTO API на Kotlin Multiplatform для JVM сервера и для Android и iOS приложения. Она читает каталог версий приложения, поэтому обе стороны собирают её одним и тем же Kotlin.

&nbsp;

## Стиль кода

Стиль проверяет [ktlint](https://github.com/pinterest/ktlint) 1.8 через Spotless, правила лежат в корневом [`.editorconfig`](../../../.editorconfig):

```sh
./gradlew spotlessCheck  # проверить
./gradlew spotlessApply  # исправить
```

- **Стиль `android_studio`** совпадает с форматером Android Studio, поэтому Ctrl+Alt+L и ktlint не спорят друг с другом.
- **120 символов в строке.**
- **Запятая после последнего элемента** в многострочных списках: так diff занимает одну строку, а строки можно свободно переставлять.
- **По одному параметру на строку**, если параметров два и больше.

Комментарии пишутся на английском, коротко, и объясняют то, чего не скажет сам код: причину, важный порядок действий, обходное решение. К очевидному коду комментариев нет.

&nbsp;

## CI

[`ci.yml`](../../../.github/workflows/ci.yml) запускается на каждый pull request и каждый push в `main` и ветки вариантов (`kmp-ready`, `kmp`), если только изменения не касаются одной лишь документации, README и картинок к ним. Три задачи выполняются параллельно:

| Задача | Шаги |
|---|---|
| Android | стиль кода (ktlint) → зависимости между модулями (`assertModuleGraph`) → неиспользуемые зависимости (`buildHealth`) → ошибки в коде и ресурсах (Android lint с правилами Compose от Slack) → [unit- и скриншот-тесты](testing.md) → сборка обоих приложений |
| iOS | общие тесты каждого KMP-модуля на iOS-симуляторе → сборка [iOS-приложения](#ios-приложение) (`xcodebuild`); на macOS, бесплатно для публичного репозитория |
| Server | стиль кода (ktlint) → API и файлы данных (тесты, включая проверку `data/*.json` и [образцов API](testing.md#контракт-с-сервером)) |

- **Каждая проверка запускается, даже если предыдущая упала**, поэтому один прогон показывает сразу все проблемы.
- **В CI предупреждения считаются ошибками**, и у Kotlin, и у lint: CI передаёт `-PwarningsAsErrors=true`, а этот флаг читают convention-плагины ([`WarningsAsErrors.kt`](../../../build-logic/src/main/kotlin/WarningsAsErrors.kt)) и сборка сервера. Локальная сборка предупреждения только выводит.
- **Неиспользуемые зависимости** находит плагин [Dependency Analysis](https://github.com/autonomousapps/dependency-analysis-gradle-plugin); его правила — какие советы он игнорирует и почему — в корневом [`build.gradle.kts`](../../../build.gradle.kts).
- **Исключения lint** лежат в корневом [`lint.xml`](../../../lint.xml): это CompositionLocal, которые проект создаёт намеренно, и предупреждение «вышла новая версия», пониженное до подсказки, чтобы выход новой версии какой-нибудь библиотеки не ронял CI.
- **Упавшие тесты видны там, куда и так смотришь**: каждый, с сообщением и номером строки, — вверху страницы прогона и рядом со строкой теста на вкладке Files changed в PR, если файл теста входит в изменения; ниже — сводка с полным стектрейсом ([action-junit-report](https://github.com/mikepenz/action-junit-report), единственное действие в CI не от GitHub и не от Gradle).
- **Android, который нужен Robolectric, кэшируется** между прогонами: Robolectric скачивает его в `~/.m2`, а эту папку кэш Gradle не покрывает. Ключ этого кэша — то, от чего он зависит: версия Robolectric и targetSdk, поэтому новая библиотека, не связанная с ним, не создаёт новую копию. Задача iOS кэширует компилятор Kotlin/Native вместе с кэшами библиотек, которые он строит при линковке (это несколько минут работы): этот кэш сохраняется после сборки Xcode, ключ — версия Kotlin и каталог версий, а новый каталог начинает с последней копии той же версии Kotlin.
- **Кэши сохраняются только при push в `main` и `kmp`**, где идёт работа; pull request'ы и остальные ветки их только читают, pull request — кэш своей базовой ветки. GitHub хранит не больше 10 ГБ кэшей на репозиторий и удаляет самые старые сверх этого, а собственная копия pull request'а не пригодилась бы ни одному другому прогону.
- **Задача iOS сохраняет кэш Gradle без очистки.** Очистка `setup-gradle` выбрасывает закэшированные тестовые программы для iOS, даже если прогон взял их из кэша, и тогда они линковались бы заново в каждом прогоне; записи, которыми не пользовались 7 дней, Gradle удаляет сам.
- **Задача останавливается через 30 минут** (iOS — через 45, серверная — через 15), а не висит 6 часов, как это по умолчанию бывает в GitHub.

Новый push в pull request отменяет его прогон, если тот ещё не закончился, а прогоны на ветках всегда доходят до конца:

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: ${{ github.event_name == 'pull_request' }}
```

[`latest-build.yml`](../../../.github/workflows/latest-build.yml) после успешного CI на push в `main`, `kmp-ready` или `kmp` собирает release-APK приложения и UI kit и публикует их как релиз `<ветка>-latest`. Ссылки на скачивание в README ведут на этот релиз. Сборка `main` — главный (Latest) релиз репозитория, сборки остальных веток публикуются как pre-release.

[`dependency-graph.yml`](../../../.github/workflows/dependency-graph.yml) после каждого push в `main` отправляет в GitHub список библиотек, с которыми выходят приложение и сервер. GitHub сверяет их с базой известных уязвимостей и, если что-то найдёт, показывает Dependabot alert на вкладке Security. Это отдельный workflow, потому что для отправки нужно право на запись, а CI работает только на чтение.

[`screenshot-comment.yml`](../../../.github/workflows/screenshot-comment.yml) выкладывает изменившиеся скриншоты в комментарий к pull request, если CI упал из-за них. Комментарий может показать картинку, только если у неё есть ссылка, поэтому картинки кладутся в ветку `screenshots/pr-<N>`; она удаляется, когда скриншоты снова совпадут или pull request закроют. Комментарий у pull request всегда один и обновляется при каждом прогоне. Этому workflow тоже нужно право на запись, поэтому код pull request он никогда не запускает, а только копирует картинки, которые сделал CI.

&nbsp;

## Подпись

Debug-ключ хранится в репозитории ([`build-logic/debug.keystore`](../../../build-logic/debug.keystore)), поэтому у сборки с любого компьютера одна и та же подпись. От неё зависят App Links: отпечаток ключа указан в `assetlinks.json`.

- **Release-сборка подписана тем же ключом**, чтобы её можно было установить на эмулятор; для публикации в магазине понадобился бы настоящий ключ.
- **В release-сборке включён R8**: он сжимает код и ресурсы.

&nbsp;

## iOS-приложение

iOS-приложение — это Xcode-проект в [`apps/shop/ios`](../../../apps/shop/ios/). Оно показывает экраны `:apps:shop:app`, который собирает их во фреймворк `ShopApp`: Run Script проекта вызывает Gradle (`embedAndSignAppleFrameworkForXcode`) до того, как Xcode компилирует код на Swift. Пока приложение показывает заглушку: экраны переезжают в общий код постепенно, по шагам ветки `kmp`.

- **Собирается на Mac** с Xcode и JDK 21: откройте `apps/shop/ios/iosApp.xcodeproj` и запустите на симуляторе, или используйте конфигурацию запуска iOS в Android Studio с плагином Kotlin Multiplatform.
- **На Linux общий код тоже компилируется под iOS** (`./gradlew compileKotlinIosSimulatorArm64`), и большинство iOS-ошибок видно ещё до CI; линковка фреймворка, iOS-тесты и сборка Xcode требуют Mac, поэтому CI выполняет их на macOS.
- **iOS 18.5 и новее**, iPhone, только портретная ориентация. К серверу приложение ходит по обычному HTTP и по IP-адресу, а iOS разрешает это только с `NSAllowsArbitraryLoads` в `Info.plist`.

&nbsp;

## Команды

Основные команды из корня проекта:

```sh
./gradlew :apps:shop:android:installDebug  # установить магазин на подключённое устройство
./gradlew :apps:uikit:installDebug         # установить UI kit
./gradlew assertModuleGraph                # проверить зависимости между модулями
./gradlew buildHealth                      # найти неиспользуемые зависимости
./gradlew lintDebug lintAndroidMain        # запустить Android lint в Android- и KMP-модулях
./gradlew testAndroidHostTest              # запустить тесты приложения на JVM, включая скриншоты
./gradlew compileKotlinIosSimulatorArm64   # скомпилировать общий код под iOS (и на Linux)
./gradlew iosSimulatorArm64Test            # запустить общие тесты на iOS-симуляторе (на Mac)
./gradlew recordRoborazziAndroidHostTest -Proborazzi.cleanupOldScreenshots=true  # записать новые скриншоты после изменения UI
cd server && ./gradlew test                # запустить тесты сервера
```

Те же проверки, что и в CI, где предупреждения считаются ошибками:

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug lintAndroidMain testAndroidHostTest :apps:shop:android:assembleDebug :apps:uikit:assembleDebug
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```
