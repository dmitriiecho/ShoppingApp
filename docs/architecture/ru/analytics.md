# Аналитика

[English version](../en/analytics.md) · [Все разделы](README.md)

Приложение отправляет события в две системы аналитики. Обе — заглушки, которые просто пишут в logcat, но код устроен так же, как с настоящими SDK.

&nbsp;

## Из чего она состоит

Ядро аналитики лежит в `:shared:analytics`, а клиенты конкретных систем — в приложении:

| Часть | Где | Что делает |
|---|---|---|
| `AnalyticsEvent` | `:shared:analytics` | имя, типизированные параметры и системы, в которые отправляется событие |
| `Analytics` | `:shared:analytics` | передаёт событие клиентам нужных систем |
| `AnalyticsSystem` | `:shared:analytics` | системы приложения: `Insights` (продуктовая аналитика), `AdTracker` (реклама) |
| `AnalyticsSystemClient` | `:shared:analytics` | код, который отправляет события в одну систему |
| `FakeInsightsClient`, `FakeAdTrackerClient` | `:apps:shop` | по одному клиенту на систему; они заменяют SDK вроде Firebase Analytics и AppsFlyer |

ViewModel вызывает `analytics.log(event)`, а `Analytics` передаёт событие клиентам только тех систем, которые в нём указаны:

```kotlin
fun log(event: AnalyticsEvent) {
    for (client in clients.filter { it.system in event.systems }) {
        try {
            client.log(event)
        } catch (e: Exception) {
            onClientError(client.system, e)  // упавшая система не мешает остальным
        }
    }
}
```

&nbsp;

## События

Каждое событие — это класс, в котором указаны его системы и параметры:

```kotlin
internal class AddToCartAnalyticsEvent(
    productId: String,
    priceCents: Long,
    screen: AnalyticsScreen,
) : AnalyticsEvent("add_to_cart") {
    override val systems = setOf(AnalyticsSystem.Insights, AnalyticsSystem.AdTracker)

    override val params = analyticsParams {
        param("product_id", productId)
        param("price_cents", priceCents)
        param("screen", screen.value)
    }
}
```

- **Куда отправлять событие, решает само событие.** На практике события добавляются часто, а системы меняются редко, и куда идёт каждое событие, задаёт план трекинга. Таблицу маршрутов в приложении пробовали, но от неё отказались.
- **Параметры типизированы**: текст, число или флаг — только те типы, которые принимает любая система. Никаких `Map<String, Any>`.
- **Имена заданы явными строками**, поэтому переименование в коде не меняет того, что получают системы.
- **Событие лежит в фиче, которая его отправляет** (`impl/…/analytics/`), или в `:shared:analytics` (`event/<тема>/`), если его отправляют две фичи и больше.

&nbsp;

## Когда отправляются события

События отправляются только из ViewModel и только после того, как действие удалось: «добавлено в корзину» отправляется, когда Room сохранил товар, а не в момент нажатия.

```kotlin
launchCartAction(
    action = { addToCart(product) },
    onSuccess = { analytics.log(AddToCartAnalyticsEvent(product.id, product.price, AnalyticsScreen.CatalogList)) },
)
```

Просмотры экранов отправляет приложение, а не экраны ([`ScreenViews.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/ScreenViews.kt)). Оно следит за стеком и сообщает о верхнем экране после каждого перехода и каждого возврата из фона. Каждая фича сама даёт имена своим экранам, а экран без имени не отправляется:

```kotlin
fun NavDestination.catalogAnalyticsScreen(): AnalyticsScreen? = when {
    hasRoute<CatalogRoutes.ProductList>() -> AnalyticsScreen.CatalogList
    hasRoute<CatalogRoutes.ProductDetails>() -> AnalyticsScreen.ProductDetails
    else -> null
}
```

&nbsp;

## Клиенты

На каждую систему приходится ровно один клиент, и это проверяется при создании `Analytics`. Иначе система без клиента молча теряла бы события, а система с двумя клиентами получала бы их дважды:

```kotlin
init {
    val systems = clients.map { it.system }
    require(systems.sorted() == AnalyticsSystem.entries) {
        "Each analytics system needs exactly one client, got $systems"
    }
}
```

&nbsp;

## Что можно отправлять

Текст, который пользователь ввёл сам, никогда не отправляется. Поиск отправляет только длину запроса: в поле поиска может оказаться что угодно, вплоть до имени или номера телефона.

```kotlin
// Only the query's length: the text itself may hold anything the user typed.
internal class ProductsSearchedAnalyticsEvent(queryLength: Int) : AnalyticsEvent("products_searched")
```

Исключение — отклонённые промокоды: код отправляется в том виде, в каком его набрали. Поле предназначено для коротких кодов, а смысл этого события как раз в том, чтобы узнать, какие несуществующие коды пробуют ввести (старые акции, опечатки).

&nbsp;

## Чего нет и почему

Некоторые вещи, которые кажутся очевидными, намеренно не сделаны:

- **Нет модуля `:core:analytics`.** Список систем принадлежит этому приложению, поэтому события и `Analytics` лежат в `:shared:analytics`; то, что осталось бы для `core`, слишком мало, чтобы выделять под это модуль.
- **Нет интерфейса `Analytics`.** Реализация всего одна; в тестах ей передаются `TestAnalyticsClient`, которые запоминают всё отправленное.
- **Нет ролей и меток у событий** (продукт, маркетинг, реклама): событие напрямую указывает свои системы.
