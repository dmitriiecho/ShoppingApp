# Модули

[English version](../en/modules.md) · [Все разделы](README.md)

Папки — это уровни, снизу вверх: `core/` → `shared/` → `feature/` → `apps/`. Модуль не зависит от уровня выше, фичи не зависят друг от друга.

&nbsp;

## Уровни

На каждом уровне свои модули и свой смысл:

| Уровень | Модули | Что в нём |
|---|---|---|
| `core/` | `designsystem`, `compose-utils`, `network`, `config` | код, который подошёл бы любому приложению: тема, иконки, общие компоненты, помощники для Compose, HTTP-клиент |
| `shared/` | `domain`, `data`, `ui`, `analytics` | код магазина, который нужен двум фичам и больше |
| `feature/` | `catalog`, `cart`, `promo`, `checkout`, `settings` | по фиче в каждом, разделённой на `ui` и `impl` |
| `apps/` | `shop`, `uikit` | приложения; от них никто не зависит |

Уровни разложены по папкам, а не только по зависимостям: если держать всё в `core/`, как делают многие проекты, не видно, какой код знает о магазине. И нет модулей «для всего» вроде `:core:common`, которые правит каждая фича: в `shared/` четыре модуля со строгими ролями.

&nbsp;

## Куда класть новый код

Код лежит в той фиче, которой он нужен. Когда он понадобился второй фиче, его переносят в `shared/`:

- **Нужен одной фиче** — остаётся в этой фиче.
- **Понадобился второй фиче** — переезжает в `shared/`: модели, репозитории и use case'ы в `:shared:domain` / `:shared:data`, UI-компоненты магазина (карточка товара, счётчик количества в корзине) в `:shared:ui`.
- **Ничего не знает о магазине** — идёт в `core/`.

Стилизованный компонент раскладывается по тому же правилу:

| Компонент | Модуль |
|---|---|
| подошёл бы другому приложению без изменений | `:core:designsystem` |
| знает слова магазина (товар, корзина, промокод, цена, остаток) и нужен двум фичам и больше | `:shared:ui` |
| знает слова магазина и нужен одной фиче | `:feature:<name>:ui` |

Фичи сами не стилизуют компоненты Material, и у каждого стилизованного компонента есть пример в приложении UI kit (`:apps:uikit`).

&nbsp;

## Фича: `ui` + `impl`

Каждая фича — это два модуля. В `impl` всё, что делает фича, а в `ui` — её UI-компоненты на простых значениях:

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

- **`ui` вынесен отдельно, чтобы UI kit мог его показать**, не видя экранов, ViewModel'ей и данных. Если у фичи таких компонентов нет, нет и модуля `ui` (`settings`).
- **Модуля `api` нет.** Фичи никогда не зависят друг от друга, так что API фичи нужно только приложению, а оно связывает фичи через колбэки (см. [Навигацию](navigation.md)).
- **От `:shared:data` зависит только `:apps:shop`.** Фичи видят интерфейсы репозиториев из `:shared:domain`, а реализации подставляет Hilt в приложении.

&nbsp;

## Что фича показывает наружу

В модуле `impl` публично только то, что подключает приложение, и всё это лежит в `presentation/navigation` (плюс имена экранов для аналитики, см. [Аналитику](analytics.md)). Остальное — экраны, ViewModel'и, UI state, use case'ы, репозитории — `internal`:

```kotlin
object CatalogRoutes {
    @Serializable data object Graph                          // приложение открывает фичу
    @Serializable internal data object ProductList
    @Serializable internal data class ProductDetails(/* ... */) : ProductDetailsRoute
}

fun CatalogNavigationScope.graph(navController: NavController, onClose: () -> Unit) { /* ... */ }
```

Один экран нужен приложению напрямую: карточку товара каталога оно показывает во вкладке корзины через inline-функцию `productDetailsScreen<T>()`. Inline-функция встраивается в код приложения, поэтому экран помечен так:

```kotlin
@Composable
@PublishedApi
internal fun ProductDetailsScreen(
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
)
```

Его может вызвать встроенный код этой функции, а вручную из приложения — нет.

&nbsp;

## Пакеты

Пакет библиотеки повторяет путь модуля, а пакет приложения — его applicationId:

| Модуль | Пакет |
|---|---|
| `:shared:ui` | `krio.systemdesign.shoppingapp.shared.ui` |
| `:feature:cart:impl` | `krio.systemdesign.shoppingapp.feature.cart.impl` |
| `:core:compose-utils` | `krio.systemdesign.shoppingapp.core.composeutils` (дефис убирается) |
| `:apps:shop` | `krio.systemdesign.shoppingapp` |

Пакет приложения привязан к applicationId, поэтому не меняется, если приложение переезжает в другую папку.

&nbsp;

## Проверка

`./gradlew assertModuleGraph` сверяет каждую зависимость между модулями со списком разрешённых в [`ModuleGraphRules.kt`](../../../build-logic/src/main/kotlin/ModuleGraphRules.kt), а CI запускает её на каждом pull request. Каждое правило — одно регулярное выражение на строку «откуда → куда»:

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

Зависимость, которую не разрешает ни одно правило, роняет сборку:

```text
[':feature:catalog:impl' -> ':feature:cart:impl'] not allowed by any of [...]
```

Новый вид связи между модулями — это решение: для него нужно новое правило в этом файле, и оно будет видно в diff.
