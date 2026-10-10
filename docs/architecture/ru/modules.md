# Модули

[English version](../en/modules.md) · [Все разделы](README.md)

Папки — это уровни, снизу вверх: `core/` → `shared/` → `feature/` → `apps/`. Модуль не зависит от уровней выше своего, а фичи не зависят друг от друга.

&nbsp;

## Уровни

У каждого уровня свои модули и своё назначение:

| Уровень | Модули | Что в нём |
|---|---|---|
| `core/` | `designsystem`, `compose-utils`, `network`, `config` | код, который подошёл бы любому приложению: тема, иконки, общие компоненты, помощники для Compose, HTTP-клиент |
| `shared/` | `domain`, `data`, `ui`, `analytics` | код магазина, который нужен двум фичам и больше |
| `feature/` | `catalog`, `cart`, `promo`, `checkout`, `settings` | по одной фиче в каждой папке, из двух модулей: `ui` и `impl` |
| `apps/` | `shop`, `uikit` | приложения; от них никто не зависит |

Уровни — это именно папки, а не только правила зависимостей: если складывать всё в `core/`, как делают многие проекты, не видно, какой код знает о магазине. Нет здесь и модулей «для всего» вроде `:core:common`, которые правит каждая фича: в `shared/` четыре модуля, и у каждого своя чёткая роль.

&nbsp;

## Куда класть новый код

Код лежит в той фиче, которой он нужен. Когда он понадобился второй фиче, его переносят в `shared/`:

- **Нужен одной фиче** — остаётся в этой фиче.
- **Понадобился второй фиче** — переезжает в `shared/`: модели, репозитории и use case'ы в `:shared:domain` / `:shared:data`, UI-компоненты магазина (карточка товара, счётчик количества в корзине) в `:shared:ui`.
- **Ничего не знает о магазине** — идёт в `core/`.

Стилизованные компоненты раскладываются по тому же правилу:

| Компонент | Модуль |
|---|---|
| подошёл бы другому приложению без изменений | `:core:designsystem` |
| использует понятия магазина (товар, корзина, промокод, цена, остаток) и нужен двум фичам и больше | `:shared:ui` |
| использует понятия магазина и нужен одной фиче | `:feature:<name>:ui` |

Фичи не стилизуют компоненты Material сами, а у каждого стилизованного компонента есть пример в приложении UI kit (`:apps:uikit`).

&nbsp;

## Фича: `ui` + `impl`

Каждая фича состоит из двух модулей. В `impl` — всё, что делает фича, а в `ui` — её UI-компоненты, которые принимают простые значения:

```kotlin
// feature/cart/impl/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

dependencies {
    implementation(project(":feature:cart:ui"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    // ...
}
```

- **`ui` вынесен в отдельный модуль, чтобы UI kit мог его показать**, не видя экранов, ViewModel и данных. Если таких компонентов у фичи нет, то нет и модуля `ui` (например, у `settings`).
- **Модуля `api` нет.** Фичи никогда не зависят друг от друга, так что API фичи нужно только приложению, а оно связывает фичи через колбэки (см. [Навигацию](navigation.md)).
- **От `:shared:data` зависит только `:apps:shop`.** Фичи видят интерфейсы репозиториев из `:shared:domain`, а реализации подставляет Hilt в приложении.

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
| `:shared:ui` | `krio.systemdesign.shoppingapp.shared.ui` |
| `:feature:cart:impl` | `krio.systemdesign.shoppingapp.feature.cart.impl` |
| `:core:compose-utils` | `krio.systemdesign.shoppingapp.core.composeutils` (дефис убирается) |
| `:apps:shop` | `krio.systemdesign.shoppingapp` |

Пакет приложения привязан к applicationId, поэтому он не меняется, если приложение переедет в другую папку.

&nbsp;

## Проверка

`./gradlew assertModuleGraph` сверяет каждую зависимость между модулями со списком разрешённых в [`ModuleGraphRules.kt`](../../../build-logic/src/main/kotlin/ModuleGraphRules.kt), а CI запускает эту проверку на каждом pull request. Каждое правило — это одно регулярное выражение для строки «откуда -> куда»:

```kotlin
allowed = arrayOf(
    """:core:.* -> :core:.*""",
    """:shared:(domain|ui|analytics) -> :core:.*""",
    """:shared:data -> :(core:.*|shared:domain)""",
    """:feature:\w+:ui -> :(core:designsystem|shared:ui)""",
    """:feature:\w+:impl -> :(core:.*|shared:(domain|ui|analytics))""",
    """:feature:(\w+):impl -> :feature:\1:ui""",   // только свой ui
    """:apps:uikit -> :(core:designsystem|shared:ui|feature:\w+:ui)""",
    """:apps:shop -> :(core|shared|feature):.*""",
)
```

Если зависимость не разрешена ни одним правилом, сборка падает:

```text
[':feature:catalog:impl' -> ':feature:cart:impl'] not allowed by any of [...]
```

Новый вид связи между модулями — это осознанное решение: для него нужно добавить правило в этот файл, и оно будет видно в diff.
