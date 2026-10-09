# Аналитика

[English version](../en/analytics.md) · [Все разделы](README.md)

Приложение отправляет события в две системы аналитики. Обе — заглушки, которые пишут в logcat, но код устроен так, как был бы с настоящими SDK.

&nbsp;

## Из чего состоит

Ядро аналитики лежит в `:shared:analytics`, а клиенты конкретных систем — в приложении:

| Часть | Где | Что делает |
|---|---|---|
| `AnalyticsEvent` | `:shared:analytics` | имя, типизированные параметры и системы, в которые идёт событие |
| `Analytics` | `:shared:analytics` | передаёт событие клиентам его систем |
| `AnalyticsSystem` | `:shared:analytics` | системы приложения: `Insights` (продуктовая аналитика), `AdTracker` (реклама) |
| `AnalyticsSystemClient` | `:shared:analytics` | код, который отправляет в одну систему |
| `FakeInsightsClient`, `FakeAdTrackerClient` | `:apps:shop` | по клиенту на систему; заменяют SDK вроде Firebase Analytics и AppsFlyer |

ViewModel вызывает `analytics.log(event)`, а `Analytics` отдаёт событие клиентам только тех систем, которые событие назвало:

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

Каждое событие — класс, который сам называет свои системы и параметры:

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

- **Куда идёт событие, решает само событие.** На практике события добавляются часто, а системы меняются редко, и маршрут каждого события задаёт план трекинга. Таблицу маршрутов в приложении пробовали и отказались.
- **Параметры типизированы**: текст, число или флаг, только типы, которые принимает любая система. Никакого `Map<String, Any>`.
- **Имена — явные строки**, поэтому переименование кода не меняет того, что получают системы.
- **Событие лежит в фиче, которая его отправляет** (`impl/…/analytics/`), или в `:shared:analytics` (`event/<тема>/`), если его отправляют две фичи и больше.

&nbsp;

## Когда отправляются события

События отправляются только из ViewModel'ей и только после успеха действия: «добавлено в корзину» пишется, когда Room сохранил товар, а не при нажатии.

```kotlin
launchCartAction(
    action = { addToCart(product) },
    onSuccess = { analytics.log(AddToCartAnalyticsEvent(product.id, product.price, AnalyticsScreen.CatalogList)) },
)
```

Просмотры экранов отправляет приложение, а не экраны ([`ScreenViews.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/ScreenViews.kt)). Оно следит за стеком и сообщает о верхнем экране после каждого перехода и каждого возврата из фона. Каждая фича называет свои экраны сама, а экран без имени не отправляется:

```kotlin
fun NavDestination.catalogAnalyticsScreen(): AnalyticsScreen? = when {
    hasRoute<CatalogRoutes.ProductList>() -> AnalyticsScreen.CatalogList
    hasRoute<CatalogRoutes.ProductDetails>() -> AnalyticsScreen.ProductDetails
    else -> null
}
```

&nbsp;

## Клиенты

На каждую систему ровно один клиент, и это проверяется при создании `Analytics`. Иначе система без клиента молча теряла бы события, а система с двумя получала бы их дважды:

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

Свободный текст, который ввёл пользователь, не отправляется. Поиск отправляет только длину запроса: в поле поиска может оказаться что угодно, имя или номер телефона.

```kotlin
// Only the query's length: the text itself may hold anything the user typed.
internal class ProductsSearchedAnalyticsEvent(queryLength: Int) : AnalyticsEvent("products_searched")
```

Исключение — отклонённые промокоды: код отправляется как набран, потому что поле предназначено для коротких кодов, а узнать, какие несуществующие коды пробуют (старые акции, опечатки), — и есть смысл этого события.

&nbsp;

## Чего нет намеренно

Несколько очевидных на вид частей не сделаны специально:

- **Нет модуля `:core:analytics`.** Список систем принадлежит этому приложению, поэтому события и `Analytics` лежат в `:shared:analytics`; то, что осталось бы в `core`, слишком мало для модуля.
- **Нет интерфейса `Analytics`.** Реализация одна; тест передаёт ей `TestAnalyticsClient`, которые записывают отправленное.
- **Нет ролей и меток событий** (продукт, маркетинг, реклама): событие напрямую называет свои системы.
