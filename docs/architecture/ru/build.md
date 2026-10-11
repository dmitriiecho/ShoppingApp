# Сборка

[English version](../en/build.md) · [Все разделы](README.md)

Общая настройка модулей вынесена в [`build-logic/`](../../../build-logic/) — подключаемую сборку (included build) с convention-плагинами. Каждый модуль применяет несколько таких плагинов и объявляет только собственные зависимости.

&nbsp;

## Convention-плагины

Для каждого вида модулей есть свой плагин:

| Плагин | Для чего | Что добавляет |
|---|---|---|
| `shoppingapp.android.application` | `:apps:shop`, `:apps:uikit` | настройку Android, targetSdk, подпись, R8 в release, языки приложения для системных настроек, [проверку графа модулей](modules.md#проверка), анализ зависимостей, предупреждения как ошибки в CI |
| `shoppingapp.android.library` | каждая Android-библиотека | compileSdk, minSdk, Java, анализ зависимостей, предупреждения как ошибки в CI, настройку тестов: unit-тесты только на debug, [Robolectric](testing.md#база-данных), [образцы API](testing.md#контракт-с-сервером), текст ошибки упавшего теста в логе |
| `shoppingapp.android.compose` | модули с Compose | компилятор Compose, BOM, Material 3, правила lint для Compose от Slack |
| `shoppingapp.metro` | модули с DI, Android и чистый Kotlin | Metro: `internal`-класс с `@Contributes*` остаётся `internal`, а `internal` binding container роняет сборку |
| `shoppingapp.android.feature` | каждый `:feature:<name>:impl` | library + Compose + Metro + serialization, библиотеки навигации и lifecycle |
| `shoppingapp.android.screenshots` | каждая библиотека с превью | [скриншот-тесты](testing.md#скриншоты), созданные из превью |
| `shoppingapp.jvm.library` | `:shared:domain`, `:shared:analytics`, `:core:config` | Kotlin JVM без Android, анализ зависимостей, предупреждения как ошибки в CI, текст ошибки упавшего теста в логе |

Поэтому build-файл модуля получается коротким:

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

Все версии указаны в [`gradle/libs.versions.toml`](../../../gradle/libs.versions.toml), и build-logic читает тот же каталог. Модули подключают плагины без указания версий.

- **Репозитории объявлены только в `settings.gradle.kts`**: если модуль объявит свои, сборка упадёт. Из Google Maven берутся только артефакты Google и AndroidX.
- **Включены configuration cache, build cache и параллельная сборка.** Build cache хранит результаты задач, и CI тоже переиспользует их из прошлых прогонов.
- **Сервер — это отдельная сборка** со своим wrapper и каталогом версий, в настройки приложения она не входит.

&nbsp;

## Стиль кода

Стиль проверяет [ktlint](https://github.com/pinterest/ktlint) 1.8 через Spotless, правила лежат в корневом [`.editorconfig`](../../../.editorconfig):

```sh
./gradlew spotlessCheck   # проверить
./gradlew spotlessApply   # исправить
```

- **Стиль `android_studio`** совпадает с форматером Android Studio, поэтому Ctrl+Alt+L и ktlint не спорят друг с другом.
- **120 символов в строке.**
- **Запятая после последнего элемента** в многострочных списках: так diff занимает одну строку, а строки можно свободно переставлять.
- **По одному параметру на строку**, если параметров два и больше.

Комментарии пишутся на английском, коротко, и объясняют то, чего не скажет сам код: причину, важный порядок действий, обходное решение. К очевидному коду комментариев нет.

&nbsp;

## CI

[`ci.yml`](../../../.github/workflows/ci.yml) запускается на каждый pull request и каждый push в `main` и ветки вариантов (`kmp-ready`, `kmp`), если только изменения не касаются одной лишь документации, README и картинок к ним. Две задачи выполняются параллельно:

| Задача | Шаги |
|---|---|
| Android | стиль кода (ktlint) → зависимости между модулями (`assertModuleGraph`) → неиспользуемые зависимости (`buildHealth`) → ошибки в коде и ресурсах (Android lint с правилами Compose от Slack) → [unit- и скриншот-тесты](testing.md) → сборка обоих приложений |
| Server | стиль кода (ktlint) → API и файлы данных (тесты, включая проверку `data/*.json` и [образцов API](testing.md#контракт-с-сервером)) |

- **Каждая проверка запускается, даже если предыдущая упала**, поэтому один прогон показывает сразу все проблемы.
- **В CI предупреждения считаются ошибками**, и у Kotlin, и у lint: CI передаёт `-PwarningsAsErrors=true`, а этот флаг читают convention-плагины ([`WarningsAsErrors.kt`](../../../build-logic/src/main/kotlin/WarningsAsErrors.kt)) и сборка сервера. Локальная сборка предупреждения только выводит.
- **Неиспользуемые зависимости** находит плагин [Dependency Analysis](https://github.com/autonomousapps/dependency-analysis-gradle-plugin); его правила — какие советы он игнорирует и почему — в корневом [`build.gradle.kts`](../../../build.gradle.kts).
- **Исключения lint** лежат в корневом [`lint.xml`](../../../lint.xml): это CompositionLocal, которые проект создаёт намеренно, и предупреждение «вышла новая версия», пониженное до подсказки, чтобы выход новой версии какой-нибудь библиотеки не ронял CI.
- **Упавшие тесты видны там, куда и так смотришь**: каждый, с сообщением и номером строки, — вверху страницы прогона и рядом со строкой теста на вкладке Files changed в PR, если файл теста входит в изменения; ниже — сводка с полным стектрейсом ([action-junit-report](https://github.com/mikepenz/action-junit-report), единственное действие в CI не от GitHub и не от Gradle).
- **Android, который нужен Robolectric, кэшируется** между прогонами: Robolectric скачивает его в `~/.m2`, а эту папку кэш Gradle не покрывает.
- **Задача останавливается через 30 минут** (серверная — через 15), а не висит 6 часов, как это по умолчанию бывает в GitHub.

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

## Команды

Основные команды из корня проекта:

```sh
./gradlew :apps:shop:installDebug      # установить магазин на подключённое устройство
./gradlew :apps:uikit:installDebug     # установить UI kit
./gradlew assertModuleGraph            # проверить зависимости между модулями
./gradlew buildHealth                  # найти неиспользуемые зависимости
./gradlew lintDebug                    # запустить Android lint во всех модулях
./gradlew test                         # запустить тесты приложения, включая скриншоты
./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true  # записать новые скриншоты после изменения UI
cd server && ./gradlew test            # запустить тесты сервера
```

Те же проверки, что и в CI, где предупреждения считаются ошибками:

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug test :apps:shop:assembleDebug :apps:uikit:assembleDebug
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```
