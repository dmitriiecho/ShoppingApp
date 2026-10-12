[English version](../en/modules.md) · [Все разделы](README.md)

> **Вариант `kmp`**: Android и iOS, Metro, Ktor
>
> Эта страница в других вариантах:
> - [`main`](https://github.com/dmitriiecho/ShoppingApp/blob/main/docs/architecture/ru/modules.md): Android, Hilt, Retrofit
> - [`kmp-ready`](https://github.com/dmitriiecho/ShoppingApp/blob/kmp-ready/docs/architecture/ru/modules.md): Android, Metro, Ktor

# Модули

Папки — это уровни, снизу вверх: `core/` → `crossfeature/` → `feature/` → `apps/`. Модуль не зависит от уровней выше своего, а фичи не зависят друг от друга.

&nbsp;

## Уровни

У каждого уровня свои модули и своё назначение:

| Уровень | Модули | Что в нём |
|---|---|---|
| `core/` | `designsystem`, `compose-utils`, `network`, `config` | код, который подошёл бы любому приложению: тема, иконки, общие компоненты, помощники для Compose, HTTP-клиент |
| `crossfeature/` | `domain`, `data`, `ui`, `analytics` | код магазина, который нужен двум фичам и больше |
| `feature/` | `catalog`, `cart`, `promo`, `checkout`, `settings` | по одной фиче в каждой папке, из двух модулей: `ui` и `impl` |
| `apps/` | `shop`, `uikit` | приложения; от них никто не зависит |

Уровни — это именно папки, а не только правила зависимостей: если складывать всё в `core/`, как делают многие проекты, не видно, какой код знает о магазине. Нет здесь и модулей «для всего» вроде `:core:common`, которые правит каждая фича: в `crossfeature/` четыре модуля, и у каждого своя чёткая роль.

Средний уровень называется `crossfeature`, а не `shared`: в Kotlin Multiplatform «shared» означает код, общий для платформ, а в ветке `kmp` такой код тоже есть.

Внутри уровней есть модули ещё двух видов:

- **Модули `testing`** держат тестовых помощников, которые нужны и тестам других модулей, рядом с модулем, к которому они относятся: `:core:network:testing`, `:crossfeature:domain:testing`. У модулей на Kotlin Multiplatform нет test fixtures, поэтому помощники получают свой модуль; зависят от него только тесты ([Тестирование](testing.md#имена-и-расположение)).
- **Приложение магазина разделено по платформам**, как советует JetBrains: в `:apps:shop:app` всё общее для обеих платформ (`App()`, навигация, нижняя панель, аналитика, основа графа приложения), в `iosMain` — точка входа и граф iOS, и он собирает фреймворк, который подключает Xcode-проект в `apps/shop/ios` ([Сборка](build.md#ios-приложение)); `:apps:shop:android` — точка входа Android (`MainActivity`, `ShoppingApp`, граф для Android, манифест, ресурсы). UI kit разделён так же: в `:apps:uikit:app` его экраны и образцы, и он собирает фреймворк для `apps/uikit/ios`, в `:apps:uikit:android` — `UiKitActivity`.

Вне уровней лежит [`contract/`](../../../contract/): DTO API, общие с сервером. Это отдельная сборка, которую подключают и приложение, и сервер, поэтому сервер, тоже отдельная сборка, может пользоваться ею без приложения. От неё зависят `:crossfeature:data` и `impl`-модули фич, которые обращаются к серверу ([Данные](data.md#договорённости-с-сервером)).

&nbsp;

## Куда класть новый код

Код лежит в той фиче, которой он нужен. Когда он понадобился второй фиче, его переносят в `crossfeature/`:

- **Нужен одной фиче** — остаётся в этой фиче.
- **Понадобился второй фиче** — переезжает в `crossfeature/`: модели, репозитории и use case'ы в `:crossfeature:domain` / `:crossfeature:data`, UI-компоненты магазина (карточка товара, счётчик количества в корзине) в `:crossfeature:ui`.
- **Ничего не знает о магазине** — идёт в `core/`.

Стилизованные компоненты раскладываются по тому же правилу:

| Компонент | Модуль |
|---|---|
| подошёл бы другому приложению без изменений | `:core:designsystem` |
| использует понятия магазина (товар, корзина, промокод, цена, остаток) и нужен двум фичам и больше | `:crossfeature:ui` |
| использует понятия магазина и нужен одной фиче | `:feature:<name>:ui` |

Фичи не стилизуют компоненты Material сами, а у каждого стилизованного компонента есть пример в приложении UI kit (`:apps:uikit:app`).

&nbsp;

## Фича: `ui` + `impl`

Каждая фича состоит из двух модулей. В `impl` — всё, что делает фича, а в `ui` — её UI-компоненты, которые принимают простые значения:

```kotlin
// feature/cart/impl/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.kmp.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:cart:ui"))
            implementation(project(":crossfeature:domain"))
            implementation(project(":crossfeature:ui"))
            implementation(project(":crossfeature:analytics"))
            implementation(project(":core:designsystem"))
            // ...
        }
    }
}
```

- **`ui` вынесен в отдельный модуль, чтобы UI kit мог его показать**, не видя экранов, ViewModel и данных. Если таких компонентов у фичи нет, то нет и модуля `ui` (например, у `settings`).
- **Модуля `api` нет.** Фичи никогда не зависят друг от друга, так что API фичи нужно только приложению, а оно связывает фичи через колбэки (см. [Навигацию](navigation.md)).
- **`:crossfeature:data` используют только графы приложений**: `:apps:shop:android` и iOS-часть `:apps:shop:app`. Фичи видят интерфейсы репозиториев из `:crossfeature:domain`, а реализации подставляет граф зависимостей приложения (Metro).
- **Модуль с графом платформы напрямую зависит от каждого модуля с вкладами в DI** (`@ContributesTo`, `@ContributesBinding` и подобными) или с `@Inject`-классами, которые создаёт граф, хотя его собственный код их не использует: `:apps:shop:android` перечисляет фичи, `:crossfeature:data`, `:core:network` и другие. Metro строит граф при компиляции этого модуля и видит только то, что есть в его compile classpath; по той же причине типы модуля, которые встречаются в его привязках, подключены как `api` (Room и DataStore в `:crossfeature:data`).

&nbsp;

## Публичный API фичи

В модуле `impl` публично только то, что подключает приложение, и всё это лежит в `presentation/navigation` (плюс имена экранов для аналитики, см. [Аналитику](analytics.md)). Всё остальное — экраны, ViewModel, UI state, use case'ы, репозитории — объявлено `internal`:

```kotlin
object CatalogRoutes {
    @Serializable data object Graph                          // приложение открывает фичу
    @Serializable internal data object ProductList
    @Serializable internal data class ProductDetails(/* ... */) : ProductDetailsRoute
}

fun CatalogNavigationScope.graph(navController: NavController, onClose: () -> Unit) { /* ... */ }
```

&nbsp;

## Пакеты

Пакет библиотеки повторяет путь модуля, а пакет приложения — его applicationId:

| Модуль | Пакет |
|---|---|
| `:crossfeature:ui` | `krio.systemdesign.shoppingapp.crossfeature.ui` |
| `:feature:cart:impl` | `krio.systemdesign.shoppingapp.feature.cart.impl` |
| `:core:compose-utils` | `krio.systemdesign.shoppingapp.core.composeutils` (дефис убирается) |
| `:apps:shop:android` | `krio.systemdesign.shoppingapp` |
| `:apps:uikit:android`, `:apps:uikit:app` | `krio.systemdesign.shoppingapp.uikit` |
| `:apps:shop:app` | `krio.systemdesign.shoppingapp`: это часть приложения, поэтому у неё пакет приложения; Android-namespace у неё по-прежнему по пути, потому что у библиотеки он не может совпадать с приложением |

Пакет приложения привязан к applicationId, поэтому он не меняется, если приложение переедет в другую папку.

&nbsp;

## Проверка

`./gradlew assertModuleGraph` сверяет каждую зависимость между модулями со списком разрешённых в [`ModuleGraphRules.kt`](../../../build-logic/src/main/kotlin/ModuleGraphRules.kt), а CI запускает эту проверку на каждом pull request. Каждое правило — это одно регулярное выражение для строки «откуда -> куда»:

```kotlin
allowed = arrayOf(
    """:core:.* -> :core:.*""",
    """:crossfeature:(domain|ui|analytics) -> :core:.*""",
    """:crossfeature:data -> :(core:.*|crossfeature:domain)""",
    """:feature:\w+:ui -> :(core:designsystem|crossfeature:ui)""",
    """:feature:\w+:impl -> :(core:.*|crossfeature:(domain|ui|analytics))""",
    """:feature:(\w+):impl -> :feature:\1:ui""",   // только свой ui
    """:apps:uikit:android -> :apps:uikit:app""",
    """:apps:uikit:(android|app) -> :(core:designsystem|crossfeature:ui|feature:\w+:ui)""",
    """:apps:shop:android -> :apps:shop:app""",
    """:apps:shop:(android|app) -> :(core|crossfeature|feature):.*""",
)
```

Если зависимость не разрешена ни одним правилом, сборка падает:

```text
[':feature:catalog:impl' -> ':feature:cart:impl'] not allowed by any of [...]
```

Новый вид связи между модулями — это осознанное решение: для него нужно добавить правило в этот файл, и оно будет видно в diff.
